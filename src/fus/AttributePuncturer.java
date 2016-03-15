package fus;
import weka.core.Instances;

public interface AttributePuncturer {
	public Instances select(Instances instances);
}
