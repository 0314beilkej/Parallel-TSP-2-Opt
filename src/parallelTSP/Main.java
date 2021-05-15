package parallelTSP;

import java.awt.List;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*****************************************************************************************/
/** 		Code found at https://github.com/jackspyder/2-opt/tree/master/src/sample    **/
/**			Modified by Julia Beilke for COSC 6060 - Parallel and Distributed Systems	**/
/** 		Project to parallelize 2-Opt approach for traveling salesman problem		**/
/*****************************************************************************************/

/* Main test class */
public class Main {

	public static void main(String[] args) {

		double startTime, time;
		// ArrayList<Point2D> cities = new ArrayList<>(Load.loadTSPLib("rd100.tsp")); //
		// alter file name here.
		ArrayList<Point2D> nearestN;
		ArrayList<Point2D> result, parallelResult;

		String[] filenames = { "rl10.tsp", "rd100.tsp", "rd400.tsp", "rl1889.tsp", "rl3000.tsp", "rl5934.tsp" };
		// String[] filenames = {"rl11849.tsp"};

		for (String filename : filenames) {
			// Read file

			ArrayList<Point2D> cities = new ArrayList<>(Load.loadTSPLib(filename));

			// Get initial tour info
			double length = Length.routeLength(cities);
			System.out.println("No. Cities = " + cities.size());
			System.out.println("Initial tour length = 		" + length);

			// Get nearest neighbor tour info
			nearestN = Neighbour.nearest(cities);
			length = Length.routeLength(nearestN);
			System.out.println("Nearest neighbour length = 	" + length);

			// Get sequential 2-opt tour info

			startTime = System.currentTimeMillis();
			result = TwoOpt.alternate(nearestN, -1, -1);
			// result = parallelTwoOpt(nearestN, 1);
			length = Length.routeLength(result);
			time = System.currentTimeMillis() - startTime;
			System.out.println("Sequential 2-opt length 	" + length + " (" + time + "ms)");

			// Get parallel 2-opt tour info
			startTime = System.currentTimeMillis();
			parallelResult = parallelTwoOpt(nearestN, 1);
			length = Length.routeLength(parallelResult);
			time = System.currentTimeMillis() - startTime;
			System.out.println("Parallel 2-opt length(p=1) = 	" + length + " (" + time + "ms)");

			// Get parallel 2-opt tour info
			startTime = System.currentTimeMillis();
			parallelResult = parallelTwoOpt(nearestN, 2);
			length = Length.routeLength(parallelResult);
			time = System.currentTimeMillis() - startTime;
			System.out.println("Parallel 2-opt length(p=2) = 	" + length + " (" + time + "ms)");

			// Get parallel 2-opt tour info
			startTime = System.currentTimeMillis();
			parallelResult = parallelTwoOpt(nearestN, 4);
			length = Length.routeLength(parallelResult);
			time = System.currentTimeMillis() - startTime;
			System.out.println("Parallel 2-opt length(p=4) = 	" + length + " (" + time + "ms)");

			// Get parallel 2-opt tour info
			startTime = System.currentTimeMillis();
			parallelResult = parallelTwoOpt(nearestN, 10);
			length = Length.routeLength(parallelResult);
			time = System.currentTimeMillis() - startTime;
			System.out.println("Parallel 2-opt length(p=10) = 	" + length + " (" + time + "ms)");

			System.out.println("\n************************************************************\n");
		}
	}

	/*
	 * Parallel2Opt Method - Written by Julia Beilke to initialize threads and
	 * runnable objects. Calculates 2-Opt tour with multithreaded appraoch.
	 */
	private static ArrayList<Point2D> parallelTwoOpt(ArrayList<Point2D> cities, int numThreads) {

		ArrayList<Point2D> bestTour = cities;
		ArrayList<Point2D> altTour;
		Double bestDist = Length.routeLength(cities);
		Double altDist;

		// Don't take more than necessary num threads
		if (cities.size() < numThreads) {
			numThreads = cities.size();
		}
		// Arrays to store threads
		ParallelTwoOpt[] parallelApproaches = new ParallelTwoOpt[numThreads];

		// Create tasks
		for (int i = 0; i < numThreads; i++) {
			parallelApproaches[i] = new ParallelTwoOpt(bestTour, numThreads, i);
		}

		// Create thread pool
		ExecutorService pool = Executors.newFixedThreadPool(numThreads);

		boolean improved = true;
		int iteration = 0;
		while (improved) {
			ArrayList<Future> futures = new ArrayList<>();
			for (int i = 0; i < numThreads; i++) {
				parallelApproaches[i].setBestTour(bestTour);
				futures.add(pool.submit(parallelApproaches[i]));
			}

			iteration++;
			// System.out.println("Iteration #" + iteration + "...");
			improved = false;
			int futurecount = 0;
			for (Future f : futures) {
				try {
					f.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// Get best tour
			for (ParallelTwoOpt p : parallelApproaches) {
				altTour = p.getBestTour();
				altDist = Length.routeLength(altTour);

				if (altDist < bestDist) {
					improved = true;
					bestTour = altTour;
					bestDist = altDist;
				}
			}
			// System.out.println("Best tour for iteration #" + iteration + " = " +
			// bestDist);
		}

		pool.shutdown();
		return bestTour;
	}
}
