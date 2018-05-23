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

