package eu.ha3.matmos.util;

/** A simple bytequeue implementation I made because I couldn't find a built-in class for this purpose... */
public class ByteQueue {
	
	byte[] data;
	int head; // where the newest element is
	int tail; // where the oldest element is
	int length; // amount of elements stored
	int capacity; // length of backing array, max amount of elements that can be held
	int initialCapacity;
	
	public ByteQueue(int capacity){
		this.capacity = this.initialCapacity = capacity;
		data = new byte[capacity];
		clear();
	}
	
	public void clear() {
		head = -1;
		tail = 0;
		length = 0;
	}
	
	public void push(byte b) {
		if(length >= capacity && capacity * 2 >= initialCapacity) {
			setCapacity(capacity * 2);
		}
		head = (head + 1) % capacity;
		data[head] = b;
		
		length++;
	}
	
	public void push4(byte b1, byte b2, byte b3, byte b4) {
		push(b1);
		push(b2);
		push(b3);
		push(b4);
	}
	
	public byte pop() {
		if(length < 1) {
			System.err.println("ByteQueue array is empty! couldn't pop");
			return Byte.MIN_VALUE;
		}
		byte b = data[tail]; 
		tail = (tail + 1) % capacity;
		length--;
		
		if(length <= capacity / 2 && capacity / 2 >= initialCapacity) {
			setCapacity(capacity / 2);
		}
		
		return b;
	}
	
	public boolean pop4(byte[] b) {
		if(length < 4) {
			//System.err.println("ByteQueue array doesn't have enough elements to pop 4!");
			return false;
		}
		for(int i = 0; i < 4; i++) {
			b[i] = pop();
		}
		return true;
	}
	
	private void setCapacity(int newCapacity) {
		byte[] newData = new byte[newCapacity];
		for(int i = tail, c = 0; c < length; i = (i + 1) % capacity, c++) {
			newData[c] = data[i];
		}
		capacity = newCapacity;
		data = newData;
		tail = 0;
		head = length - 1;
	}	
	
	public int length() {
		return length;
	}
}