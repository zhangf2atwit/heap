package edu.wit.cs.comp2350;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Adds floating point numbers with varying precision
 * 
 * Wentworth Institute of Technology COMP 2350 Assignment 2
 * 
 */

public class A2 {

	// array and variables for heap
	static float[] minHeap;
	static int size;
	static int index;

	// build the MinHeap
	private static void MinHeap(int maxCapacity) {
		A2.size = 0;
		A2.minHeap = new float[maxCapacity];
		A2.index = 0;
	}

	// TODO: document this method
	public static float heapAdd(float[] a) {
		// TODO: implement this method

		// if the input is null, return 0
		if (a.length == 0)
			return 0;

		// Build a min heap
		// Variable to store the total sum of the heap, = root
		MinHeap(a.length);

		// insert all the input
		for (int i = 0; i < a.length; i++) {
			insert(a[i]);
		}

		// variable for store the Sum for every two mins each time
		float tempSum;

		for (int i = 0; i < a.length - 1; i++) {
			float min1 = A2.extract();
			float min2 = A2.extract();
			tempSum = min1 + min2;

			// call the insert function to put the temp sum at right place in the heap
			insert(tempSum);
		}
		return minHeap[0]; // return the error-minimized sum of floats
	}

	// get the parent index
	private static int getParent(int i) {
		int posParent = (i - 1) / 2;
		return posParent;
	}

	// check if the tree have leaf
	private static boolean isLeaf(int i) {
		if ((getRightChild(i) >= size) && (getLeftChild(i) >= size))
			return true;
		else
			return false;
	}

	// get the left child index
	private static int getLeftChild(int i) {
		return (2 * i + 1);
	}

	// get the right child index
	private static int getRightChild(int i) {
		return (2 * i + 2);
	}

	// swap the two elements, help build the min heap
	private static void swapMin(int a, int b) {
		float temp;
		temp = minHeap[a];
		minHeap[a] = minHeap[b];
		minHeap[b] = temp;
	}

	// for insert another outside (heap) element
	private static void insert(float tempSum) {
		index = size;
		minHeap[index] = tempSum;
		int current = index;
		while (minHeap[current] < minHeap[getParent(current)]) {
			swapMin(current, getParent(current));
			current = getParent(current);
		}
		size++;
	}

	private static float extract() {
		// since its a min heap, so root = minimum
		float popped = minHeap[0];
		minHeap[0] = minHeap[--size];
		sortMinHeap(0);
		return popped;
	}

	// build the min heap with the compare child and parents
	private static void sortMinHeap(int pos) {
		if (!isLeaf(pos)) {
			if (minHeap[pos] > minHeap[getLeftChild(pos)] || minHeap[pos] > minHeap[getRightChild(pos)]) {
				if (minHeap[getLeftChild(pos)] < minHeap[getRightChild(pos)]) {
					swapMin(pos, getLeftChild(pos));
					sortMinHeap(getLeftChild(pos));
				} else {
					swapMin(pos, getRightChild(pos));
					sortMinHeap(getRightChild(pos));
				}
			}
		}
	}

	/********************************************
	 * 
	 * You shouldn't modify anything past here
	 * 
	 ********************************************/

	// sum an array of floats sequentially - high rounding error
	public static float seqAdd(float[] a) {
		float ret = 0;

		for (int i = 0; i < a.length; i++)
			ret += a[i];

		return ret;
	}

	// sort an array of floats and then sum sequentially - medium rounding error
	public static float sortAdd(float[] a) {
		Arrays.sort(a);
		return seqAdd(a);
	}

	// scan linearly through an array for two minimum values,
	// remove them, and put their sum back in the array. repeat.
	// minimized rounding error, inefficient operations
	public static float min2ScanAdd(float[] a) {
		int min1, min2;
		float tmp;

		if (a.length == 0)
			return 0;

		for (int i = 0, end = a.length; i < a.length - 1; i++, end--) {

			if (a[0] < a[1]) {
				min1 = 0;
				min2 = 1;
			} // initialize
			else {
				min1 = 1;
				min2 = 0;
			}

			for (int j = 2; j < end; j++) { // find two min indices
				if (a[min1] > a[j]) {
					min2 = min1;
					min1 = j;
				} else if (a[min2] > a[j]) {
					min2 = j;
				}
			}

			tmp = a[min1] + a[min2]; // add together
			if (min1 < min2) { // put into first slot of array
				a[min1] = tmp; // fill second slot from end of array
				a[min2] = a[end - 1];
			} else {
				a[min2] = tmp;
				a[min1] = a[end - 1];
			}
		}

		return a[0];
	}

	// read floats from a Scanner
	// returns an array of the floats read
	private static float[] getFloats(Scanner s) {
		ArrayList<Float> a = new ArrayList<Float>();

		while (s.hasNextFloat()) {
			float f = s.nextFloat();
			if (f >= 0)
				a.add(f);
		}
		return toFloatArray(a);
	}

	// copies an ArrayList to an array
	private static float[] toFloatArray(ArrayList<Float> a) {
		float[] ret = new float[a.size()];
		for (int i = 0; i < ret.length; i++)
			ret[i] = a.get(i);
		return ret;
	}

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);

		System.out.printf("Enter the adding algorithm to use ([h]eap, [m]in2scan, se[q], [s]ort): ");
		char algo = s.next().charAt(0);

		System.out
				.printf("Enter the non-negative floats that you would like summed, followed by a non-numeric input: ");
		float[] values = getFloats(s);
		float sum = 0;

		s.close();

		if (values.length == 0) {
			System.out.println("You must enter at least one value");
			System.exit(0);
		} else if (values.length == 1) {
			System.out.println("Sum is " + values[0]);
			System.exit(0);

		}

		switch (algo) {
		case 'h':
			sum = heapAdd(values);
			break;
		case 'm':
			sum = min2ScanAdd(values);
			break;
		case 'q':
			sum = seqAdd(values);
			break;
		case 's':
			sum = sortAdd(values);
			break;
		default:
			System.out.println("Invalid adding algorithm");
			System.exit(0);
			break;
		}

		System.out.printf("Sum is %f\n", sum);

	}

}
