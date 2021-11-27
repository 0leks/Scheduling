package ok.schedule;

import java.util.*;

import javax.swing.*;

import ok.schedule.model.Day;
import ok.schedule.model.Employee;

public class Assigner {

	private static final int NOBODY = -1;
  class Bag {
		public List<Employee> employees;

		public Bag() {
			employees = new LinkedList<>();
		}
	}

	// Function to return the next random number
	static int getNum(ArrayList<Integer> v) {
		// Size of the vector
		int n = v.size();

		// Make sure the number is within
		// the index range
		int index = (int) (Math.random() * n);

		// Get random number from the vector
		int num = v.get(index);

		// Remove the number from the vector
		v.set(index, v.get(n - 1));
		v.remove(n - 1);

		// Return the removed number
		return num;
	}

	// Function to generate n
	// non-repeating random numbers
	static LinkedList<Integer> generateRandomPermutation(int n) {
		ArrayList<Integer> v = new ArrayList<>(n);

		// Fill the vector with the values
		// 1, 2, 3, ..., n
		for (int i = 0; i < n; i++)
			v.add(i);

		LinkedList<Integer> toret = new LinkedList<>();
		// While vector has elements
		// get a random number from the vector and print it
		while (v.size() > 0) {
			toret.add(getNum(v));
		}
		return toret;
	}

	private boolean isPossibleDay(	Employee e, Bag[] bag, int day, Bag[][] assignments, int position,
									int EMPLOYEES_PER_POSITION) {
		return bag[day].employees.contains(e) && assignments[day][position].employees.size() < EMPLOYEES_PER_POSITION;
	}

	public List<Integer> getPossibleDays(	Employee e, Bag[] bag, Bag[][] assignments, int position,
											int EMPLOYEES_PER_POSITION) {
		List<Integer> possDays = new LinkedList<>();
		for (int day = 0; day < bag.length; day++) {
			if (isPossibleDay(e, bag, day, assignments, position, EMPLOYEES_PER_POSITION)) {
				possDays.add(day);
			}
		}
		return possDays;
	}

	public boolean assignToBestPosition(Employee e, Bag[] bag, Bag[][] assignments, int EMPLOYEES_PER_POSITION) {
		// find which position works the most
		int maxNum = -1;
		int bestPosition = -1;
		List<Integer> positions = generateRandomPermutation(assignments[0].length);
		while (!positions.isEmpty()) {
			int position = positions.remove((int) (Math.random() * positions.size()));
			int num = getPossibleDays(e, bag, assignments, position, EMPLOYEES_PER_POSITION).size();
			if (num > maxNum) {
				maxNum = num;
				bestPosition = position;
			}
		}
		System.out.println("assigning to " + bestPosition + " with " + maxNum + " poss days");
		if (maxNum == 0) {
			return false;
		}
		for (int day = 0; day < bag.length; day++) {
			if (isPossibleDay(e, bag, day, assignments, bestPosition, EMPLOYEES_PER_POSITION)) {
				System.out.println("day " + day);
				assignments[day][bestPosition].employees.add(e);
				bag[day].employees.remove(e);
			} else {
				System.out.println("failed to assign");
			}
		}
		return true;
	}

	public Day[][] generateSchedule(Day[][] days, List<Employee> employees) {

		for (int week = 0; week < days.length; week++) {
			int NUM_DAYS = 5;
			Bag[] bag = new Bag[NUM_DAYS];
			for (int dayIndex = 0; dayIndex < NUM_DAYS; dayIndex++) {
				bag[dayIndex] = new Bag();
			}
			for (Employee e : employees) {
				for (int dayIndex = 0; dayIndex < NUM_DAYS; dayIndex++) {
					if (e.available(dayIndex)) {
						bag[dayIndex].employees.add(e);
					}
				}
			}
			int NUM_POSITIONS = Constants.NUM_POSITIONS;
			int EMPLOYEES_PER_POSITION = 1;
			Bag[][] assignments = new Bag[NUM_DAYS][NUM_POSITIONS];
			for (int dayIndex = 0; dayIndex < NUM_DAYS; dayIndex++) {
				for (int position = 0; position < NUM_POSITIONS; position++) {
					assignments[dayIndex][position] = new Bag();
				}
			}
			// First assign employees that have a preferred position
			for (int day = 0; day < assignments.length; day++) {
				for (int employeeIndex = bag[day].employees.size() - 1; employeeIndex >= 0; employeeIndex--) {
					Employee e = bag[day].employees.get(employeeIndex);
					if (e.isPositionLocked(day)) {
						if (e.getLockedPosition(day) >= assignments[day].length) {
							JOptionPane.showMessageDialog(null, "ERROR: " + e.getName()
									+ " has invalid locked position: " + (e.getLockedPosition(day) + 1));
						} else {
							assignments[day][e.getLockedPosition(day)].employees.add(e);
							bag[day].employees.remove(e);
							System.out.println("Assigned " + e.getName() + " to position " + e.getLockedPosition(day));
						}
					}
				}
			}
			// then assign the rest of the employees to random positions
			List<Integer> possibleDays = new LinkedList<>();
			for (int i = 0; i < 5; i++) {
				possibleDays.add(i);
			}
			while (!possibleDays.isEmpty()) {
				int day = possibleDays.remove((int) (Math.random() * possibleDays.size()));
				System.out.println("Handling day " + day);
				while (!bag[day].employees.isEmpty()) {
					Employee e = bag[day].employees.get((int) (Math.random() * bag[day].employees.size()));
					System.out.println("Handling employee " + e.getName());
					boolean success = assignToBestPosition(e, bag, assignments, EMPLOYEES_PER_POSITION);
					if (!success) {
						System.out.println("failed to assign " + e + ", removing from list");
						bag[day].employees.remove(e);
					}
				}
			}

			for (int day = 0; day < assignments.length; day++) {
				days[week][day].clearAssignments();
				for (int position = 0; position < assignments[day].length; position++) {
					for (Employee e : assignments[day][position].employees) {
						days[week][day].assign(e);
					}
				}
			}
		}
		return days;
	}

	public class Node {
		int[] assignments;
		int numAssigned;

		public Node() {
			assignments = new int[Constants.NUM_POSITIONS];
			for (int i = 0; i < assignments.length; i++) {
				assignments[i] = NOBODY;
			}
			numAssigned = 0;
		}

		public void assign(int id) {
			assignments[numAssigned] = id;
			numAssigned++;
		}

		public void assignAll(int id) {
			for (numAssigned = 0; numAssigned < Constants.NUM_POSITIONS; numAssigned++) {
				assignments[numAssigned] = id;
			}
		}

		public int getAssignment(int position) {
			return assignments[position];
		}
	}
}
