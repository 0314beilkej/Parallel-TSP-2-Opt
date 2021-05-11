package sample;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class ParallelTwoOpt implements Runnable {
	private ArrayList<Point2D> totalBestTour;
	private int numThreads;
	private double totalBestDist;
	private int tId;

	public ParallelTwoOpt(ArrayList<Point2D> cities, int numThreads, int id) {
		this.totalBestTour = cities;
		this.numThreads = numThreads;
		this.tId = id;
	}

	public void run() {
		this.totalBestDist = Length.routeLength(totalBestTour);
		this.totalBestTour = alternate(totalBestTour, numThreads, -1, -1, 0);

	}

	public void run1() {
		// Thread t = Thread.currentThread();
		// this.tId = Integer.valueOf(t.getName());
		double newDist;

		totalBestDist = Length.routeLength(totalBestTour);
		cities = this.totalBestTour;
		// System.out.println("Thread #" + tId + " running on cities with best dist = "
		// + bestDist);

		// initialize inner/outer loops avoiding adjacent calculations and making use of
		// problem symmetry to half total comparisons.

		for (int i = tId + 1; i < cities.size() - 2; i += numThreads) {
			for (int j = i + 1; j < cities.size() - 1; j++) {

				// check distance of line A,B + line C,D against A,C + B,D if there is
				// improvement, call swap method.
				if ((cities.get(i).distance(cities.get(i - 1))
						+ cities.get(j + 1).distance(cities.get(j))) >= (cities.get(i).distance(cities.get(j + 1))
								+ cities.get(i - 1).distance(cities.get(j)))) {

					newTour = swap(cities, i, j); // pass arraylist and 2 points to be swapped.

					newDist = Length.routeLength(newTour);

					if (newDist < totalBestDist) { // if the swap results in an improved distance, increment counters
													// and
													// update distance/tour
						totalBestTour = newTour;
						totalBestDist = newDist;
					}

				}

			}
		}
		// this.bestTour = cities;
		// System.out.println("Thread #" + tId + " finished. Best length "+ bestDist);
	}

	private ArrayList<Point2D> alternate(ArrayList<Point2D> cities, int numThreads, int iIn, int jIn, int depth) {
		ArrayList<Point2D> newTour, bestTour;
		double bestDist = Length.routeLength(cities);
		double origDist = bestDist;
		bestTour = cities;

		double newDist;
		int swaps = 1;
		int improve = 0;
		int iterations = 0;
		long comparisons = 0;


		// System.out.println("Thread #" + tId + " running on cities with best length "
		// + bestDist);

		// initialize inner/outer loops avoiding adjacent calculations and making use of
		// problem symmetry to half total comparisons.
		
		if (depth != 0) {
			//numThreads = 1;
		}
		while (swaps > 0) {
			swaps = 0;
			for (int i = tId+1; i < cities.size() - 2; i += numThreads) {
				for (int j = i; j < cities.size() - 1; j ++) {
					if (i != iIn && j != jIn) {
						comparisons++;
						// check distance of line A,B + line C,D against A,C + B,D if there is
						// improvement, call swap method.
						if ((cities.get(i).distance(cities.get(i - 1)) + cities.get(j + 1)
								.distance(cities.get(j))) >= (cities.get(i).distance(cities.get(j + 1))
										+ cities.get(i - 1).distance(cities.get(j)))) {

							newTour = swap(cities, i, j); // pass arraylist and 2 points to be swapped.

							newDist = Length.routeLength(newTour);

							if (newDist < bestDist) { // if the swap results in an improved distance, increment counters
														// and
														// update distance/tour

								depth++;
								ArrayList<Point2D> childTour = alternate(newTour, numThreads, i, j, depth);
								double childDist = Length.routeLength(childTour);
								if (childDist < newDist) {
									newDist = childDist;
									newTour = childTour;
								}

								if (newDist < bestDist) {
									bestTour = newTour;
									bestDist = newDist;
									//swaps++;
									// improve++;
								}

							}
						}
					}
				}
			}
		}
		// System.out.println("Thread #" + tId + " running on cities with best length "
		// + bestDist);
		
		return bestTour;
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

	public ArrayList<Point2D> getBestTour() {
		return this.totalBestTour;
	}

	public void setBestTour(ArrayList<Point2D> newTour) {
		this.totalBestTour = newTour;
	}
}
