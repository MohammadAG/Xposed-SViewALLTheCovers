S-View ALL the Covers!
========================

Forces S-View to work with 3rd party cases on Samsung's 4.4.2, bypassing verification.

Technical details
========================

Samsung's KitKat update started checking sysfs for values populated when a case is connected.

The aforementioned sysfs paths are:

* Color: /sys/devices/w1_bus_master1/w1_master_check_color

* Height: /sys/devices/w1_bus_master1/w1_master_check_height

* Width: /sys/devices/w1_bus_master1/w1_master_check_width

* Type: /sys/devices/w1_bus_master1/w1_master_check_id

* Verification: /sys/devices/w1_bus_master1/w1_master_verify_mac

Cover type is 2 for 3rd party cases, further digging into Samsung's code revealed that the possible IDs are:

* 0 = standard flip cover

* 1 = S-View cover

* 2 = No cover

* 3 = S-View cover with Qi charging

This module forces type 1, and disables verification by always returning true.
