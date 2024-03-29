package parallelTSP;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/*****************************************************************************************/
/** 		Code found at https://github.com/jackspyder/2-opt/tree/master/src/sample    **/
/**			Modified by Julia Beilke for COSC 6060 - Parallel and Distributed Systems	**/
/** 		Project to parallelize 2-Opt approach for traveling salesman problem		**/
/*****************************************************************************************/


public class TwoOpt {
	public static ArrayList<Point2D> alternate(ArrayList<Point2D> cities, int iIn, int jIn) {
		ArrayList<Point2D> newTour, bestTour;
		double bestDist = Length.routeLength(cities);
		double origDist = bestDist;
		double newDist;
		int swaps = 1;
		int improve = 0;
		int iterations = 0;
		long comparisons = 0;

		bestTour = cities;

		/*
		 * Copy of TwoOpt Alternate method, modified by Julia Beilke to recurse on
		 * improving paths
		 */
		for (int i = 1; i < cities.size() - 2; i++) {
			for (int j = i + 1; j < cities.size() - 1; j++) {
				if (i != iIn && j != jIn) {
					comparisons++;
					// check distance of line A,B + line C,D against A,C + B,D if there is
					// improvement, call swap method.
					if ((cities.get(i).distance(cities.get(i - 1))
							+ cities.get(j + 1).distance(cities.get(j))) >= (cities.get(i).distance(cities.get(j + 1))
									+ cities.get(i - 1).distance(cities.get(j)))) {

						newTour = swap(cities, i, j); // pass arraylist and 2 points to be swapped.

						newDist = Length.routeLength(newTour);

						if (newDist < bestDist) { // if the swap results in an improved distance, increment counters and
													// update distance/tour
							ArrayList<Point2D> childTour = alternate(newTour, i, j);
							double childDist = Length.routeLength(childTour);
							if (childDist < newDist) {
								newDist = childDist;
								newTour = childTour;
							}

							if (newDist < bestDist) {
								bestTour = newTour;
								bestDist = newDist;
								improve++;
							}
						}

					}

				}
			}

			// System.out.println("Best tour for iteration " + iterations + " = " +
			// bestDist);
			iterations++;
		}
		// System.out.println("Total comparisons made: " + comparisons);
		// System.out.println("Total improvements made: " + improve);
		// System.out.println("Total iterations made: " + iterations);
		cities = bestTour;
		return cities;
	}

	private static ArrayList<Point2D> swap(ArrayList<Point2D> cities, int i, int j) {
		// conducts a 2 opt swap by inverting the order of the points between i and j
		ArrayList<Point2D> newTour = new ArrayList<>();

		// take array up to first point i and add to newTour
		int size = cities.size();
		for (int c = 0; c <= i - 1; c++) {
			newTour.add(cities.get(c));
		}

		// invert order between 2 passed points i and j and add to newTour
		int dec = 0;
		for (int c = i; c <= j; c++) {
			newTour.add(cities.get(j - dec));
			dec++;
		}

		// append array from point j to end to newTour
		for (int c = j + 1; c < size; c++) {
			newTour.add(cities.get(c));
		}

		return newTour;
	}
}