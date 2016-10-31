/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph;

/**
 * A link is an edge in the graph in which is stored where it goes, and what the capacity is.
 * It is commonly used in an adjacency list as element.
 */
public class Link {

	private int linkTo;
	private int linkCapacity;

	public Link(int linkTo){
		this.linkTo = linkTo;
		this.linkCapacity = 1;
	}

	Link(int linkTo, int capacity){
		this.linkTo = linkTo;
		this.linkCapacity = capacity;
	}

	public int getLinkCapacity() {
		return linkCapacity;
	}

	void increaseLinkCapacity(int amount) {
		linkCapacity += amount;
	}

	void decreaseLinkCapacity(int amount) {
		linkCapacity -= amount;
	}

	public int getLinkTo() {
		return linkTo;
	}

}
