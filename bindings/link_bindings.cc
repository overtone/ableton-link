// #include <jni.h>
#include "link_bindings.hpp"

using namespace std;

AbletonLink AL;

// bool toBool (int num) {
//   return (1 == num);
// }

void AbletonLink_enable(bool enableBool)
{
  AL.setLinkEnable(enableBool);
  // return;
}


bool AbletonLink_isEnabled()
{
  return AL.getLinkEnable();			       
}

double AbletonLink_getBeat()
{
  return AL.getBeat();
}

void AbletonLink_setBeat(double beat)
{
  AL.setBeat(beat);
  // return;
}

void AbletonLink_setBeatForce(double beat)
{
  AL.setBeatForce(beat);
  // return;
}

double AbletonLink_getPhase()
{
  return AL.getPhase();
}

double AbletonLink_getBpm()
{
  return AL.getBpm();
}

void AbletonLink_setBpm(double bpm)
{
  AL.setBpm(bpm);
}

int AbletonLink_getNumPeers()
{
  return AL.getNumPeers();
}

double AbletonLink_getQuantum()
{
  return AL.getQuantum();
}

void AbletonLink_setQuantum(double quantum)
{
  AL.setQuantum(quantum); 
}

void AbletonLink_update()
{
  AL.update();
}
