#include "link_bindings.hpp"


extern "C" DLL_EXPORT AbletonLink *AbletonLink_ctor();

AbletonLink *AbletonLink_ctor() {
  return new AbletonLink();
}

void AbletonLink_enable(AbletonLink *self, bool enableBool)
{
  self->setLinkEnable(enableBool);
}

bool AbletonLink_isEnabled(AbletonLink *self)
{
  return self->getLinkEnable();          
}
double AbletonLink_getBeat(AbletonLink *self)
{
  return self->getBeat();
}
void AbletonLink_setBeat(AbletonLink *self, double beat)
{
  self->setBeat(beat);
  // return;
}
void AbletonLink_setBeatForce(AbletonLink *self, double beat)
{
  self->setBeatForce(beat);
  // return;
}
double AbletonLink_getPhase(AbletonLink *self)
{
  return self->getPhase();
}
double AbletonLink_getBpm(AbletonLink *self)
{
  return self->getBpm();
}
void AbletonLink_setBpm(AbletonLink *self, double bpm)
{
  self->setBpm(bpm);
}
int AbletonLink_getNumPeers(AbletonLink *self)
{
  return self->getNumPeers();
}
double AbletonLink_getQuantum(AbletonLink *self)
{
  return self->getQuantum();
}
void AbletonLink_setQuantum(AbletonLink *self, double quantum)
{
  self->setQuantum(quantum); 
}
void AbletonLink_update(AbletonLink *self)
{
  self->update();
}

