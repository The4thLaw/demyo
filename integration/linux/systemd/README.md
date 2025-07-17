# Systemd integration

This guide provides a basic way to integrate Demyo with `systemd`, to ease administration on servers.

## Assumptions

This guide assumes that:
- Demyo will run under a `demyo:demyo` user
- Demyo is installed (or symlinked) in /home/demyo/opt/demyo/demyo-latest
- The WAR is symlinked in /home/demyo/opt/demyo/demyo-web-latest.war

## Installation

As root, or sudoing your way into each command:
- Install the `demyo.service` file in this folder in `/etc/systemd/system/demyo.service`
- Change the values if needed based on your installation if you do not strictly meet the assumptions above.
- Ensure any modification you might have made in the file is correct
```shell
systemd-analyze verify /etc/systemd/system/demyo.service
```
- Reload systemd
```shell
systemctl daemon-reload
```
- Enable the service
```shell
systemctl enable demyo
```
- From then on, you can use these commands to start, check the status, check the logs and stop demyo
```shell
systemctl start demyo
service demyo start

systemctl status demyo
service demyo status

journalctl -u demyo

systemctl stop demyo
service demyo stop
```


## References

See `systemctl(1)`, `journalctl(1)`, `systemd.exec(5)`, `systemd.directives(7)`.
