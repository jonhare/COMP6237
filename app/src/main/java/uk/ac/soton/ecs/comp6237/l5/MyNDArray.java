package uk.ac.soton.ecs.comp6237.l5;

import org.nd4j.linalg.api.ndarray.INDArray;

public class MyNDArray {
	INDArray inner;

	public MyNDArray(INDArray inner) {
		this.inner = inner;
	}

	MyNDArray plus(MyNDArray other) {
		return new MyNDArray(inner.add(other.inner));
	}

	MyNDArray minus(MyNDArray other) {
		return new MyNDArray(inner.sub(other.inner));
	}

	@Override
	public String toString() {
		return inner.toString();
	}
}
