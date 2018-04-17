# Ableton Link for Clojure
```
"Link is a technology that keeps devices in time over a local network, so you can forget the hassle of setting up and focus on playing music."
```
[https://www.ableton.com/en/link/](https://www.ableton.com/en/link/)

`overtone.ableton-link` is a glue between the official open source c++ library and Clojure via JNA.
This library provides precompiled binaries which have been tested on various computers.

Special thanks to [2bbb](https://github.com/2bbb) for his wonderful [node-abletonlink](https://github.com/2bbb/node-abletonlink)

## Supported platforms
* Windows x86_64 on JVM x86_64
* OsX     x86_64 on JVM x86_64
* Linux   x86_64 on JVM x86_64

(other platforms upon request, open a ticket)

## Firewall
For Windows and Machintosh, make sure to accept the firewall dialog that appears as you enable ableton-link,
for linux users that use firewall, make sure that port `20808` is open, with iptables this would be
```
iptables -A INPUT -p tcp --dport 20808 -j ACCEPT
service iptables restart
```
(to make iptable rule permanent, please use google)


## API
## `overtone.ableton-link`
* `enable-link`: `[boolean] => nil`
If passed true, will enable link and network discovery, false for turning link off.
* `link-enabled?`: `[] => booolean`
Returns true if link is enabled.
* `get-beat`: `[] => number`
Reads from the clock and returns the current beat value.
* `set-beat`: `[number] => nil`
Sets the current beat to a given value next time when all peers in sessions are togeather on same phase,
if there's only one client in session, this will take effect immedietly.
* `set-beat-force`: `[number] => nil`
USE WITH CAUTION, this will set the current beat for all in session on a give value, will take
effect immedietly and could cause disconinuity (may be anti-social behaviour).
* `get-phase`: `[] => number`
Reads from the session and returns the phase of the beat.
* `get-bpm`: `[] => number`
Reads from the session and returns the current bpm value
* `set-bpm`: `[number] => nil`
Commits new bpm value to the session, takes effect immedietly.
* `get-num-peers`: `[] => number`
Reads from the session and returns the number of peers connected.
* `set-quantum`: `[number] => nil`
Commits new quantum to the session, quantum is the quantization of a bar,
similar to division of a musical time signature.
