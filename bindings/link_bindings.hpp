#include <jni.h>
#include <ableton/Link.hpp>

#ifndef link_bindings_H
#define link_bindings_H
#endif

class AbletonLink {
  ableton::Link link;
  double beat{0.0};
  double phase{0.0};
  double bpm{120.0};
  double quantum{4.0};
  
public:
  AbletonLink()
    : AbletonLink(120.0) {}
  AbletonLink(double bpm)
    : AbletonLink(bpm, 4.0) {}
  AbletonLink(double bpm, double quantum)
    : AbletonLink(bpm, quantum, true) {}
  AbletonLink(double bpm, double quantum, bool enable)
    : link(bpm)
    , bpm(bpm)
    , quantum(quantum)
  {}
  explicit AbletonLink(const AbletonLink *other)
    : AbletonLink(other->bpm, other->quantum, other->getLinkEnable()) {}
  
  bool getLinkEnable() const { return link.isEnabled(); }

  void setLinkEnable(bool enable) { link.enable(enable); }

  double getBeat() const { return beat; }
  
  void setBeat(double beat) {
    const auto time = link.clock().micros();
    auto timeline = link.captureAppSessionState();
    timeline.requestBeatAtTime(beat, time, quantum);
    link.commitAppSessionState(timeline);
  }

  void setBeatForce(double beat) {
    const auto time = link.clock().micros();
    auto timeline = link.captureAppSessionState();
    timeline.forceBeatAtTime(beat, time, quantum);
    link.commitAppSessionState(timeline);
  }

  double getPhase() const { return phase; }

  double getBpm() const { return bpm; }

  void setBpm(double bpm) {
    this->bpm = bpm;
    const auto time = link.clock().micros();
    auto timeline = link.captureAppSessionState();
    timeline.setTempo(bpm, time);
    link.commitAppSessionState(timeline);
  }

  std::size_t getNumPeers() const { return link.numPeers(); }

  void setQuantum(double quantum) { this->quantum = quantum; }
  
  double getQuantum() const { return quantum; }
  
  void update() {
    const auto time = link.clock().micros();
    auto timeline = link.captureAppSessionState();
            
    beat = timeline.beatAtTime(time, quantum);
    phase = timeline.phaseAtTime(time, quantum);
    bpm = timeline.tempo();
  };  
};


extern "C" {
  void Java_AbletonLink_enable(bool enableBool);
  // JNIEXPORT void   JNICALL Java_AbletonLink_enable(JNIEnv *, jobject, jboolean);
  // JNIEXPORT bool   JNICALL Java_AbletonLink_isEnabled(JNIEnv *, jobject);
  // JNIEXPORT double JNICALL Java_AbletonLink_getBeat(JNIEnv *, jobject);
  // JNIEXPORT void   JNICALL Java_AbletonLink_setBeat(JNIEnv *, jobject, jdouble);
  // JNIEXPORT void   JNICALL Java_AbletonLink_setBeatForce(JNIEnv *, jobject, jdouble);
  // JNIEXPORT double JNICALL Java_AbletonLink_getPhase(JNIEnv *, jobject);
  // JNIEXPORT double JNICALL Java_AbletonLink_getBpm(JNIEnv *, jobject);
  // JNIEXPORT void   JNICALL Java_AbletonLink_setBpm(JNIEnv *, jobject, jdouble);
  // JNIEXPORT int    JNICALL Java_AbletonLink_getNumPeers(JNIEnv *, jobject);
  // JNIEXPORT void   JNICALL Java_AbletonLink_setQuantum(JNIEnv *, jobject, jdouble);
  // JNIEXPORT double JNICALL Java_AbletonLink_getQuantum(JNIEnv *, jobject);
  // JNIEXPORT void   JNICALL Java_AbletonLink_update(JNIEnv *, jobject);
  
}
