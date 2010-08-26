package benchmark.domain;

public class IndirectionLevel extends IndirectionLevel_Base {
    
    public  IndirectionLevel() {
        super();
    }

    public IndirectionLevel(int value) {
	setIntContainer(new IntContainer(value));
    }
    
}
