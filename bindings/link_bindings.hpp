/*

  Copyright (C) 2018, Hlöðver Sigurðsson
  
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/


#pragma once

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


#ifdef _WIN32
#   define DLL_EXPORT __declspec(dllexport)
#   define STDCALL _stdcall
#else
#   define DLL_EXPORT
#   define STDCALL
#endif

extern "C" {
  DLL_EXPORT void STDCALL AbletonLink_enable(AbletonLink *self, bool enableBool);
  DLL_EXPORT bool STDCALL AbletonLink_isEnabled(AbletonLink *self);
  DLL_EXPORT double STDCALL AbletonLink_getBeat(AbletonLink *self);
  DLL_EXPORT void STDCALL AbletonLink_setBeat(AbletonLink *self, double beat);
  DLL_EXPORT void STDCALL AbletonLink_setBeatForce(AbletonLink *self, double beat);
  DLL_EXPORT double STDCALL AbletonLink_getPhase(AbletonLink *self);
  DLL_EXPORT double STDCALL AbletonLink_getBpm(AbletonLink *self);
  DLL_EXPORT void STDCALL AbletonLink_setBpm(AbletonLink *self, double bpm);
  DLL_EXPORT int STDCALL AbletonLink_getNumPeers(AbletonLink *self);
  DLL_EXPORT void STDCALL AbletonLink_setQuantum(AbletonLink *self, double quantum);
  DLL_EXPORT double STDCALL AbletonLink_getQuantum(AbletonLink *self);
  DLL_EXPORT void STDCALL AbletonLink_update(AbletonLink *self);
  
}
