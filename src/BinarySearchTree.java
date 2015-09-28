import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class BinarySearchTree<T extends Comparable<T>> {
	private BinaryNode root;
	private final BinaryNode NULL_NODE = new BinaryNode();
	private int size;
	private boolean altered = false;

	// Constructor for a tree with a null_node
	public BinarySearchTree() {
		root = NULL_NODE; // NULL_NODE;
	}

	public boolean remove(T obj) {
		if (obj == null) {
			throw new IllegalArgumentException();
		}
		if (root == NULL_NODE) {
			return false;
		}
		root = root.remove(obj, root);
		return true;
	}
	
	// Inserts obj into the Binary Tree
	public boolean insert(T obj) {
		if (obj == null) {
			throw new IllegalArgumentException();
		}

		if (root.contains(obj)) {
			return false;
		}
		
		root = root.insert(obj);
		size++;
		altered = true;
		return true;
	}

	// Checks to see if a node in the tree contains this object
	public boolean contains(T obj) {
		if (obj == null) {
			throw new IllegalArgumentException();
		}
		return root.contains(obj);
	}

	// Checks if the tree is empty
	public boolean isEmpty() {
		return root == NULL_NODE;
	}

	// Returns the size of the tree
	public int size() {
		return size;
	}

	// return the height of the tree
	public int height() {
		return root.height();
	}

	// Changes arraylist to a string
	public String toString() {
		if (isEmpty()) {
			return "[]";
		}
		return this.toArrayList().toString();
	}

	// Puts the binary tree nodes into an arraylist
	public ArrayList<T> toArrayList() {
		ArrayList<T> list = new ArrayList<T>();
		root.toArrayList(list);
		return list;
	}

	// Puts the binary tree nodes into an array
	public Object[] toArray() {
		return this.toArrayList().toArray();
	}

	// Uses an preordered Iterator on the tree
	public Iterator<T> preOrderIterator() {
		altered = false;
		return new PreOrderIterator();
	}

	// Uses an inorder Iterator on the tree
	public Iterator<T> iterator() {
		altered = false;
		return new iterator();
	}

	class BinaryNode {
		private T data;
		private BinaryNode left;
		private BinaryNode right;

		// Constructor for node with no data
		public BinaryNode() {
			this.data = null;
			this.left = NULL_NODE;
			this.right = NULL_NODE;
		}

		// Constructor for node with data
		public BinaryNode(T element) {
			this.data = element;
			this.left = NULL_NODE;// NULL_NODE;
			this.right = NULL_NODE;// NULL_NODE;
		}

		// Inserts a node into the tree
		public BinaryNode insert(T obj) {
			
			if (this == NULL_NODE) {
				return new BinaryNode(obj);
			}

			if (this.data.compareTo(obj) < 0) {
				right = right.insert(obj);
				return this;
			}
			if (this.data.compareTo(obj) > 0) {
				left = left.insert(obj);
				return this;
			}
			return this; // shouldn't make it here
		}
		
		public BinaryNode remove(T obj, BinaryNode parent) {
			altered = true;
			BinaryNode temp = NULL_NODE;
			
			if(parent == NULL_NODE){
				throw new NoSuchElementException();
			}
			
			if(obj.compareTo(parent.data)< 0){
				parent.left = remove(obj, parent.left);
				
			}else if(obj.compareTo(parent.data) > 0){
				parent.right = remove(obj, parent.right);
			}else if(parent.left != NULL_NODE && parent.right != NULL_NODE){
				parent.data = findMin(parent.right).data;
				parent.right = removeMin(parent.right);
			}else{
				parent = (parent.left != NULL_NODE) ? parent.left : parent.right;
			}
			return parent;
		}
		
		//Gets the max node from the right subtree
		public BinaryNode getMax() {
			BinaryNode temp = this;
			while (temp.right != NULL_NODE) {
				temp = temp.right;
			}
			return temp;
		}
		
		//gets the minimum node from the tree of the node passed
		public BinaryNode findMin(BinaryNode obj){
			if(obj != NULL_NODE){
				while(obj.left != NULL_NODE){
					obj = obj.left;
				}
			}
			return obj;
		}
		
		//Removes the minimum node from the tree passes(left most)
		public BinaryNode removeMin(BinaryNode obj){
			if(obj == null){
				throw new NoSuchElementException();
			}else if(obj.left != NULL_NODE){
				obj.left = removeMin(obj.left);
				return obj;
			}
			else
				return obj.right;
		}
		
		//Inorder 
		public void toArrayList(ArrayList<T> list) {
			if (this == NULL_NODE) {
				return;
			}
			this.left.toArrayList(list);
			list.add(this.data);
			this.right.toArrayList(list);
		}

		// Checks if the tree contains data
		public boolean contains(T obj) {
			if (this == NULL_NODE) {
				return false;
			}

			boolean inNode = this.data.equals(obj);
			boolean inLeft = this.left.contains(obj);
			boolean inRight = this.right.contains(obj);
			return inNode || inLeft || inRight;
		}

		public int height() {
			if (this == NULL_NODE) {
				return -1;
			}
			return 1 + Math.max(left.height(), right.height());
		}
	}

	// Returns the data of the tree in preordered fashion using a stack
	public class PreOrderIterator implements Iterator<T> {
		private Stack<BinaryNode> storage = new Stack<BinaryNode>();
		private BinaryNode temp = new BinaryNode();
		private boolean nextCalled = false;

		public PreOrderIterator() {
			if (root != NULL_NODE) {
				storage.push(root);
			}
		}

		@Override
		public boolean hasNext() {
			return (!storage.isEmpty());
		}

		@Override
		public T next() {
			if (altered) {
				throw new ConcurrentModificationException();
			}
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			temp = storage.pop();
			if (temp.right != NULL_NODE) {
				storage.push(temp.right);
			}
			if (temp.left != NULL_NODE) {
				storage.push(temp.left);
			}

			nextCalled = true;
			return temp.data;
		}
		
		public void remove(){
			if(!nextCalled){
				throw new IllegalStateException();
			}
			if(BinarySearchTree.this.isEmpty()){
				throw new IllegalStateException();
			}
			BinarySearchTree.this.remove(root.data);
		}
	}

	// Returns the data of the tree in in-Order fashion using a stack
	public class iterator implements Iterator<T> {
		private Stack<BinaryNode> storage = new Stack<BinaryNode>();
		private boolean nextCalled = false;

		public iterator() {
			if (root != NULL_NODE) {
				BinaryNode node = root;
				while (node != NULL_NODE) {
					storage.push(node);
					node = node.left;
				}
			}
		}

		@Override
		public boolean hasNext() {
			return (!storage.isEmpty());
		}

		@Override
		public T next() {
			if (altered) {
				throw new ConcurrentModificationException();
			}
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			BinaryNode top = storage.pop();
			if (top.right != NULL_NODE) {
				BinaryNode node = top.right;
				while (node != NULL_NODE) {
					storage.push(node);
					node = node.left;
				}
			}
			nextCalled = true;
			return top.data;
		}
		
		public void remove(){
			if(!nextCalled){
				throw new IllegalStateException();
			}
			if(BinarySearchTree.this.isEmpty()){
				throw new IllegalStateException();
			}
			BinarySearchTree.this.remove(root.data);
		}
	}
}
