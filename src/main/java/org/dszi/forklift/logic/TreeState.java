package org.dszi.forklift.logic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import org.dszi.forklift.models.Grid;
import org.dszi.forklift.models.TreeItem;

/**
 *
 * @author Slawek
 */
public class TreeState {

	public ArrayList<MoveActionTypes> treesearch(TreeItem startItem, Point destPoint, Grid grid) {
		List points = new ArrayList<>();
		Queue<TreeItem> fringe = new PriorityQueue<>(1, idComparator);
		startItem.SetPriority(0);
		startItem.SetCost(0);
		fringe.add(startItem);

		while (!fringe.isEmpty()) {
			TreeItem elem = fringe.poll();

			if (goalTest(elem.GetPoint(), destPoint)) {
				ArrayList<MoveActionTypes> actions = new ArrayList();
				TreeItem item = elem;
				while (item.GetParent() != null) {
					actions.add(item.GetAction());
					item = item.GetParent();
				}
				Collections.reverse(actions);
				return actions;
			}

			for (TreeItem item : successor(grid, elem)) {
				item.SetParent(elem);
				item.SetCost(cost(elem));
				item.SetPriority(item.GetCost() + heurestic(item.GetPoint(), destPoint));

				if (!points.contains(item.GetPoint())) {
					points.add(item.GetPoint());
					fringe.add(item);
				}
			}
		}

		return new ArrayList();
	}

	private Boolean goalTest(Point srcPoint, Point descPoint) {
		return srcPoint.x == descPoint.x && srcPoint.y == descPoint.y;
	}

	private int cost(TreeItem item) {
		return item.GetCost() + 1;
	}

	private int heurestic(Point currentPoint, Point destPoint) {
		int dx = Math.abs(currentPoint.x - destPoint.x);
		int dy = Math.abs(currentPoint.y - destPoint.y);

		return dx + dy;
	}

	public static Comparator<TreeItem> idComparator = new Comparator<TreeItem>() {

		@Override
		public int compare(TreeItem t1, TreeItem t2) {
			return (int) (t1.GetPriority() - t2.GetPriority());
		}
	};

	private List<TreeItem> successor(Grid grid, TreeItem item) {
		List<TreeItem> actions = new ArrayList();
		Point point = item.GetPoint();

		if (point.y > 0) {
			if (grid.GetObject(point.x, point.y - 1) == null) {
				actions.add(new TreeItem(new Point(point.x, point.y - 1), MoveActionTypes.TOP));
			}
		}

		if (point.x > 0) {
			if (grid.GetObject(point.x - 1, point.y) == null) {
				actions.add(new TreeItem(new Point(point.x - 1, point.y), MoveActionTypes.LEFT));
			}
		}

		if (point.x < grid.GetWidth() - 1) {
			if (grid.GetObject(point.x + 1, point.y) == null) {
				actions.add(new TreeItem(new Point(point.x + 1, point.y), MoveActionTypes.RIGHT));
			}
		}

		if (point.y < grid.GetHeight() - 1) {
			if (grid.GetObject(point.x, point.y + 1) == null) {
				actions.add(new TreeItem(new Point(point.x, point.y + 1), MoveActionTypes.BOTTOM));
			}
		}

		return actions;
	}
}
