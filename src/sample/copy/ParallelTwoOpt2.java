package sample.copy;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class ParallelTwoOpt2 implements Runnable {
	private ArrayList<Point2D> cities, bestTour, newTour;
	private int numThreads;
	private double bestDist;
	private int tId;

	public ParallelTwoOpt2(ArrayList<Point2D> cities, int numThreads) {
		this.bestTour = cities;
		this.numThreads = numThreads;

	}

	public void run() {
		Thread t = Thread.currentThread();
		this.tId = Integer.valueOf(t.getName());
		double newDist;
		int swaps = 1;
		int improve = 0;
		int iterations = 0;
		long comparisons = 0;

		bestDist = Length.routeLength(bestTour);
		cities = bestTour;
		// System.out.println("Thread #" + tId + " running on cities with best length "
		// + bestDist);

		// initialize inner/outer loops avoiding adjacent calculations and making use of
		// problem symmetry to half total comparisons.
		for (int i = tId + 1; i < cities.size() - 2; i = i + numThreads) {
			for (int j = i + 1; j < cities.size() - 1; j++) {
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
						cities = newTour;
						bestDist = newDist;
						swaps++;
						improve++;
					}
				}
			}
		}
		// System.out.println("Thread #" + tId + " running on cities with best length "
		// + bestDist);

		this.bestTour = cities;
	}

	public void alternate(ArrayList<Point2D> cities, int numThreads) {

		double newDist;
		int swaps = 1;
		int improve = 0;
		int iterations = 0;
		long comparisons = 0;

		bestDist = Length.routeLength(cities);
		// System.out.println("Thread #" + tId + " running on cities with best length "
		// + bestDist);

		// initialize inner/outer loops avoiding adjacent calculations and making use of
		// problem symmetry to half total comparisons.
		for (int i = tId + 1; i < cities.size() - 2; i = i + numThreads) {
			for (int j = i + 1; j < cities.size() - 1; j++) {
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
						cities = newTour;
						bestDist = newDist;
						swaps++;
						improve++;
					}
				}
			}
		}
		// System.out.println("Thread #" + tId + " running on cities with best length "
		// + bestDist);

		this.bestTour = cities;
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
		return this.bestTour;
	}
	
	public void setBestTour(ArrayList<Point2D> newTour) {
		this.bestTour = newTour;
	}
}
