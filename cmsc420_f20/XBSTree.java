package cmsc420_f20;

import java.util.ArrayList;

/** XBSTree (skeletal) implementation.
 *
 * MODIFY THE FOLLOWING CLASS.
 *
 * You are free to make whatever changes you like or to create additional
 * classes and files, but avoid reusing/modifying the other files given in this
 * folder.
 */

public class XBSTree<Key extends Comparable<Key>, Value> {

	// -------------------- ABSTRACT NODE CLASS --------------------
	
	private abstract class Node { // generic node (purely abstract)
		Key key;
		abstract Value find(Key x); // No function body for these!
		abstract Node insert(Key x, Value v) throws Exception; 
		abstract Node delete(Key x, InternalNode parent) throws Exception;
		abstract ArrayList<String> getPreorderList(ArrayList<String> arr); 
		abstract Value getMin(); 
		abstract Value getMax();
		abstract Key getMinKey(); // Similar to getMin, but will return the smallest Key instead of the smallest Value
		abstract Key getMaxKey(); // Similar to getMax, but will return the largest Key instead of the largest Value
		abstract Value findDown(Key x); 
		abstract Value findUp(Key x);
		abstract void clear();
	}
	
	// -------------------- INTERNAL NODE FUNCTIONS --------------------
	
	private class InternalNode extends Node { // Internal Node
		Node left;
		Node right;
		
		public InternalNode(Key key, Node left, Node right) {
			this.key = key;
			this.left = left;
			this.right = right;
		}
		
		public Value find(Key x) {
			if (x.compareTo(this.key) < 0) {
				return this.left.find(x);
			}
			else {
				return this.right.find(x);
			}
		}
		
		public Node insert(Key x, Value v) throws Exception {
			if (x.compareTo(this.key) < 0) {
				this.left = this.left.insert(x, v);
			}
			else {
				this.right = this.right.insert(x, v);
			}
			return this;
		}
		
		public Node delete(Key x, InternalNode parent) throws Exception { 
			int comp = x.compareTo(this.key);
			Node replace;
			
			if (comp >= 0) {
				replace = this.right.delete(x, this);
			} else {
				replace = this.left.delete(x, this);
			}
			
			if (parent == null) {
				return replace;
			} else if (this == parent.right) {
				parent.right = replace;
			} else {
				parent.left = replace;
			}
			return parent;
		}
		
		public ArrayList<String> getPreorderList(ArrayList<String> arr) {
			arr.add("(" + this.key.toString() + ")");
			arr = this.left.getPreorderList(arr);
			arr = this.right.getPreorderList(arr);
			return arr;
		} 
		
		public Value getMin() {  
			return this.left.getMin();
		}
		
		public Value getMax() { 
			return this.right.getMax();
		}
		
		public Key getMinKey() {
			return this.left.getMinKey();
		}
		
		public Key getMaxKey() {
			return this.right.getMaxKey();
		}
		
		public Value findDown(Key x) { 
			int comp = x.compareTo(this.key);
			
			if (comp <= 0) {
				if (this.left.findDown(x) == null) { // If external node that is NOT the correct one:
					if (x.compareTo(this.key) == 0) {
						return find(this.key);
					} else if (x.compareTo(this.key) > 0) {
						return this.getMax();
					} else {
						return this.getMin();
					}
				} else {
					if (x.compareTo(this.key) == 0) {
						if (find(this.key) == null) {
							return this.left.findDown(x);
						} else {
							return find(this.key);
						}
					} else {
						return this.left.findDown(x);
					} // Else, meaning that we just looked at an internal node, just continue the find process.
				}
			} else {
				if (this.right.findDown(x) == null) { // If the external node that is NOT the correct one:
					if (x.compareTo(this.key) > 0) {
						return this.getMax();
					} else {
						return find(this.key);
					}
				} else { 
					if (x.compareTo(this.key) ==  0) {
						return find(this.key);
					} else {
						return this.right.findDown(x);
					} // Else, meaning that we just looked at an internal node, just continue the find process. 
				}
			}
		}
		
		// Same logic from findDown applies to findUp, just with few differences to accommodate for certain cases.
		public Value findUp(Key x) { 
			int comp = x.compareTo(this.key);
			
			if (comp >= 0) {
				if (this.right.findUp(x) == null) {
					if (x.compareTo(this.key) == 0) {
						return find(this.key);
					} else if (x.compareTo(this.key) > 0) {
						return null;
					} else {
						return this.getMin();
					}
				} else {
					if (x.compareTo(this.key) ==  0) {
						if (find(this.key) == null) {
							return this.right.findUp(x);
						} else {
							return find(this.key);
						}
					} else {
						return this.right.findUp(x);
					}
				}
			} else {
				if (this.left.findUp(x) == null) {
					 if (x.compareTo(this.left.key) < 0) {
						 return this.getMin();
					 } else {
						 return find(this.key);
					 }
				} else {
					if (x.compareTo(this.key) == 0) {
						return find(this.key);
					} else {
						return this.left.findUp(x);
					}
				}
			}
		}
		
		public void clear() { 
			this.key = null;
			this.left.clear();
			this.right.clear();
		}
	}
	
	// -------------------- EXTERNAL NODE FUNCTIONS --------------------
	
	private class ExternalNode extends Node { // External Node
		Value value; 
		
		public ExternalNode(Key key, Value value) {
			this.key = key;
			this.value = value;
		}
		
		public Value find(Key x) {
			if (x.compareTo(this.key) == 0) {
				return this.value;
			}
			else return null;
		}
		
		public Node insert(Key x, Value v) throws Exception {
			if (x.compareTo(this.key) == 0) {
				throw new Exception("Duplicate Key Error!");
			}
			else {
				Node q = new ExternalNode(x, v);
				if (x.compareTo(this.key) < 0) { 
					return new InternalNode(key, q, this);
				}
				else {
					return new InternalNode(x, this, q);
				}
			}
		}
		
		public Node delete(Key x, InternalNode parent) throws Exception { 
			int comp = x.compareTo(this.key);
			if (comp != 0) {
				throw new Exception("Non-Existent Key Error!");
			} else if (parent == null) { // root node 
				return null;
			} else if (this == parent.left) {
				return parent.right;
			} else {
				return parent.left;
			}
		}
		
		public ArrayList<String> getPreorderList(ArrayList<String> arr) {
			arr.add("[" + this.key.toString() + " " + this.value.toString() + "]");
			return arr;
		}
		
		public Value getMin() { 
			return this.value;
		}
		
		public Value getMax() { 
			return this.value;
		}
		
		public Key getMinKey() {
			return this.key;
		}
		
		public Key getMaxKey() {
			return this.key;
		}
		
		public Value findDown(Key x) { 
			if (x.compareTo(this.key) == 0) {
				return this.value;
			} else {
				return null;
			}
		}
		
		public Value findUp(Key x) { 
			if (x.compareTo(this.key) == 0) {
				return this.value;
			} else {
				return null;
			}
		}
		
		public void clear() { 
			this.key = null;
			this.value = null;
		}
		
	}
	
	// -------------------- PUBLIC XBSTree FUNCTIONS --------------------
	
	Node root; // defaults to null 
	
	public XBSTree() { 
		this.root = null;
	}
	
	public Value find(Key x) { 
		if (this.root == null) { 
			return null;
		}
		else {
			return this.root.find(x);
		}
	}
	
	public void insert(Key x, Value v) throws Exception { 
		if (this.root == null) {
			this.root = new ExternalNode(x, v);
		}
		else {
			this.root = this.root.insert(x,  v);
		}
	}
	
	public void delete(Key x) throws Exception { 
		if (this.root == null) {
			return;
		} else {
			this.root = this.root.delete(x, null);
		}
	}
	
	public ArrayList<String> getPreorderList() { 
		ArrayList<String> arr = new ArrayList<>();
		if (this.root == null) {
			return arr;
		}
		else {
			return this.root.getPreorderList(arr);
		}
	}
	
	public Value getMin() { 
		if (this.root == null) {
			return null;
		}
		else {
			return this.root.getMin();
		}
	}
	
	public Value getMax() { 
		if (this.root == null) {
			return null;
		}
		else {
			return this.root.getMax();
		}
	} 
	
	public Key getMinKey() {
		if (this.root == null) {
			return null;
		} else {
			return this.root.getMinKey();
		}
	}
	
	public Key getMaxKey() {
		if (this.root == null) {
			return null;
		} else {
			return this.root.getMaxKey();
		}
	}
	
	public Value findDown(Key x) { 
		if (this.root == null) {
			return null;
		} else if (x.compareTo(this.root.getMinKey()) < 0) { // If x is smaller than the smallest key in the tree.
			return null;
		} else {
			return this.root.findDown(x);
		}
	}
	
	public Value findUp(Key x) { 
		if (this.root == null) {
			return null;
		} else if (x.compareTo(this.root.getMaxKey()) > 0) { // If x is larger than the largest key in the tree
			return null;
		} else {
			return this.root.findUp(x);
		}
	}
	
	public void clear() { 
		if (this.root == null) {
			return;
		} else {
			this.root.clear();
			this.root = null;
		}
	} 

}
