public class AbletonLink {

    static {
        System.loadLibrary("abletonlink");
    }

    public native void enable(boolean enableBool);
    public native boolean isEnabled();
    public native double getBeat();
    public native void setBeat(double beat);
    public native void setBeatForce(double beat);
    public native double getPhase();
    public native double getBpm();
    public native void setBpm(double bpm);
    public native int  getNumPeers();
    public native void setQuantum(double quantum);
    public native double getQuantum();
    public native void update();

}
