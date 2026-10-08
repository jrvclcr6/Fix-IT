package com.example.data.repository

import com.example.data.model.Cause
import com.example.data.model.Issue
import com.example.data.model.Option
import com.example.data.model.Question
import com.example.data.model.RepairDifficulty
import com.example.data.model.SolutionStep

private fun option(id: String, letter: String, text: String, vararg weights: Pair<String, Int>) =
    Option(id, letter, text, causeWeights = mapOf(*weights))

private fun question(id: String, text: String, vararg options: Option) = Question(id, text, options.toList())

private fun step(number: Int, title: String, description: String, note: String? = null) =
    SolutionStep(number, title, description, tipOrWarning = note)

internal fun expandedIssues(): List<Issue> = listOf(
    Issue(
        "issue_battery_01", "cat_hardware", "Laptop battery not charging",
        "The battery percentage is stuck, drops while plugged in, or Windows says plugged in but not charging.", "battery_alert",
        listOf(
            question("q_battery_1", "What happens when the charger is connected?",
                option("opt_battery_1a", "A", "No charging light and Windows does not detect the adapter", "cause_battery_adapter" to 5),
                option("opt_battery_1b", "B", "Charging light appears but the percentage does not increase", "cause_battery_health" to 5),
                option("opt_battery_1c", "C", "It charges only when the plug is held at an angle", "cause_battery_adapter" to 5),
                option("opt_battery_1d", "D", "It stops at 60% or 80%", "cause_battery_health" to 3)),
            question("q_battery_2", "What is the battery's physical condition?",
                option("opt_battery_2a", "A", "The case or trackpad is bulging", "cause_battery_health" to 5).copy(
                    isDangerous = true,
                    dangerWarning = "SWOLLEN BATTERY HAZARD: Shut down, unplug the charger, and stop using the device. Do not press or puncture the battery."
                ),
                option("opt_battery_2b", "B", "Battery looks normal but is more than three years old", "cause_battery_health" to 4),
                option("opt_battery_2c", "C", "Battery and charger look normal", "cause_battery_adapter" to 2, "cause_battery_health" to 2))
        ), listOf("charger", "power adapter", "battery percentage", "plugged in not charging", "swollen battery")
    ),
    Issue(
        "issue_drive_01", "cat_hardware", "SSD or hard drive not detected",
        "A storage drive is missing from Windows, Disk Management, or the BIOS/UEFI setup.", "storage",
        listOf(
            question("q_drive_1", "Where is the drive missing?",
                option("opt_drive_1a", "A", "Missing in both BIOS/UEFI and Windows", "cause_drive_connection" to 5),
                option("opt_drive_1b", "B", "Visible in BIOS but missing in File Explorer", "cause_drive_partition" to 5),
                option("opt_drive_1c", "C", "Visible in Disk Management as unallocated or offline", "cause_drive_partition" to 5)),
            question("q_drive_2", "Did anything happen before it disappeared?",
                option("opt_drive_2a", "A", "The PC was moved or the drive was newly installed", "cause_drive_connection" to 4),
                option("opt_drive_2b", "B", "Windows was reinstalled or updated", "cause_drive_partition" to 3),
                option("opt_drive_2c", "C", "The drive clicked, froze, or became very slow", "cause_drive_connection" to 3))
        ), listOf("ssd missing", "hard disk", "disk management", "nvme", "sata", "bios storage")
    ),
    Issue(
        "issue_boot_01", "cat_software", "Windows won't boot",
        "The computer reaches the logo, Automatic Repair, or a recovery screen but never reaches the desktop.", "restart_alt",
        listOf(
            question("q_boot_1", "How far does startup get?",
                option("opt_boot_1a", "A", "Automatic Repair or recovery screen appears", "cause_boot_recovery" to 5),
                option("opt_boot_1b", "B", "Windows logo spins forever", "cause_boot_files" to 4),
                option("opt_boot_1c", "C", "It says no boot device", "cause_boot_files" to 5)),
            question("q_boot_2", "What changed before the boot problem?",
                option("opt_boot_2a", "A", "A Windows update or driver was installed", "cause_boot_recovery" to 4),
                option("opt_boot_2b", "B", "A drive was cloned, replaced, or added", "cause_boot_files" to 4),
                option("opt_boot_2c", "C", "Nothing that I know of", "cause_boot_files" to 2, "cause_boot_recovery" to 2))
        ), listOf("boot loop", "automatic repair", "no boot device", "startup repair", "windows logo")
    ),
    Issue(
        "issue_app_crash_01", "cat_software", "App freezes or keeps crashing",
        "One application closes unexpectedly, stops responding, or fails immediately after launch.", "apps",
        listOf(
            question("q_app_1", "How broad is the problem?",
                option("opt_app_1a", "A", "Only one app crashes", "cause_app_cache" to 5),
                option("opt_app_1b", "B", "Several apps started crashing after an update", "cause_app_runtime" to 5),
                option("opt_app_1c", "C", "The whole PC freezes too", "cause_app_runtime" to 3)),
            question("q_app_2", "What have you already tried?",
                option("opt_app_2a", "A", "Nothing yet", "cause_app_cache" to 3),
                option("opt_app_2b", "B", "Reinstalled the app but it still crashes", "cause_app_runtime" to 4),
                option("opt_app_2c", "C", "Cleared the app cache and it improved temporarily", "cause_app_cache" to 5))
        ), listOf("not responding", "software crash", "application closes", "freeze")
    ),
    Issue(
        "issue_wifi_drop_01", "cat_internet", "Wi-Fi keeps disconnecting",
        "The wireless connection drops, reconnects repeatedly, or becomes unstable in certain rooms.", "signal_wifi_bad",
        listOf(
            question("q_wdrop_1", "Where does the connection drop?",
                option("opt_wdrop_1a", "A", "Only far from the router", "cause_wifi_signal" to 5),
                option("opt_wdrop_1b", "B", "Even beside the router", "cause_wifi_driver" to 5),
                option("opt_wdrop_1c", "C", "Only on this computer", "cause_wifi_driver" to 5)),
            question("q_wdrop_2", "When is it most unstable?",
                option("opt_wdrop_2a", "A", "When Bluetooth or a microwave is in use", "cause_wifi_signal" to 4),
                option("opt_wdrop_2b", "B", "After sleep or wake", "cause_wifi_driver" to 4),
                option("opt_wdrop_2c", "C", "At random times on every device", "cause_wifi_signal" to 3))
        ), listOf("wifi drops", "wireless unstable", "disconnect", "weak signal")
    ),
    Issue(
        "issue_ethernet_01", "cat_internet", "Ethernet says unidentified network",
        "A wired connection shows no internet, network cable unplugged, or an invalid IP address.", "settings_ethernet",
        listOf(
            question("q_eth_1", "What do the Ethernet port lights do?",
                option("opt_eth_1a", "A", "No lights on either end", "cause_ethernet_link" to 5),
                option("opt_eth_1b", "B", "Lights blink but Windows says unidentified network", "cause_ethernet_ip" to 5),
                option("opt_eth_1c", "C", "The light appears only when the cable is moved", "cause_ethernet_link" to 5)),
            question("q_eth_2", "Does the same router port work with another device?",
                option("opt_eth_2a", "A", "Yes", "cause_ethernet_ip" to 4),
                option("opt_eth_2b", "B", "No", "cause_ethernet_link" to 4),
                option("opt_eth_2c", "C", "I have not tested it", "cause_ethernet_link" to 2, "cause_ethernet_ip" to 2))
        ), listOf("lan", "wired internet", "unidentified network", "network cable unplugged", "169.254")
    ),
    Issue(
        "issue_slow_01", "cat_performance", "Computer starts and runs very slowly",
        "Startup takes several minutes and everyday apps remain sluggish even when CPU usage is not maxed out.", "hourglass_bottom",
        listOf(
            question("q_slow_1", "Which part feels slowest?",
                option("opt_slow_1a", "A", "Startup and opening files", "cause_slow_storage" to 5),
                option("opt_slow_1b", "B", "Only the first few minutes after sign-in", "cause_startup_apps" to 5),
                option("opt_slow_1c", "C", "Everything, all the time", "cause_slow_storage" to 3, "cause_startup_apps" to 2)),
            question("q_slow_2", "How full is the Windows drive?",
                option("opt_slow_2a", "A", "Less than 10% free", "cause_slow_storage" to 5),
                option("opt_slow_2b", "B", "Plenty of free space", "cause_startup_apps" to 3),
                option("opt_slow_2c", "C", "I am not sure", "cause_slow_storage" to 2))
        ), listOf("slow pc", "slow laptop", "long startup", "disk 100", "startup apps")
    ),
    Issue(
        "issue_flicker_01", "cat_display", "Screen flickers or goes black briefly",
        "The picture flashes, dims, or loses signal for a moment while the computer keeps running.", "monitor",
        listOf(
            question("q_flicker_1", "When does the flicker happen?",
                option("opt_flicker_1a", "A", "When the cable or laptop lid moves", "cause_flicker_cable" to 5),
                option("opt_flicker_1b", "B", "After a graphics update or when apps open", "cause_flicker_driver" to 5),
                option("opt_flicker_1c", "C", "At a specific refresh rate", "cause_flicker_driver" to 4)),
            question("q_flicker_2", "Does a screenshot capture the visual problem?",
                option("opt_flicker_2a", "A", "No, the screenshot looks normal", "cause_flicker_cable" to 4),
                option("opt_flicker_2b", "B", "Yes, the screenshot also looks wrong", "cause_flicker_driver" to 4),
                option("opt_flicker_2c", "C", "I have not tested it", "cause_flicker_cable" to 2, "cause_flicker_driver" to 2))
        ), listOf("screen flashing", "black briefly", "refresh rate", "display driver")
    ),
    Issue(
        "issue_artifact_01", "cat_display", "Colored lines or graphics artifacts",
        "Blocks, dots, lines, or distorted colors appear on the screen, especially during games.", "broken_image",
        listOf(
            question("q_artifact_1", "Where are the artifacts visible?",
                option("opt_artifact_1a", "A", "Only in games or 3D apps", "cause_artifact_gpu" to 5),
                option("opt_artifact_1b", "B", "Even in BIOS or on the startup logo", "cause_artifact_gpu" to 5),
                option("opt_artifact_1c", "C", "Only on one monitor", "cause_artifact_monitor" to 5)),
            question("q_artifact_2", "What changes the symptom?",
                option("opt_artifact_2a", "A", "Lowering GPU load or temperature helps", "cause_artifact_gpu" to 4),
                option("opt_artifact_2b", "B", "Changing the cable or input helps", "cause_artifact_monitor" to 4),
                option("opt_artifact_2c", "C", "Nothing changes it", "cause_artifact_gpu" to 2, "cause_artifact_monitor" to 2))
        ), listOf("gpu artifacts", "colored lines", "screen blocks", "distorted display")
    ),
    Issue(
        "issue_printer_01", "cat_peripheral", "Printer is offline or won't print",
        "Print jobs remain queued even though the printer is powered on and has paper.", "print",
        listOf(
            question("q_printer_1", "How is the printer connected?",
                option("opt_printer_1a", "A", "Wi-Fi or network", "cause_printer_connection" to 5),
                option("opt_printer_1b", "B", "USB cable", "cause_printer_queue" to 3, "cause_printer_connection" to 3),
                option("opt_printer_1c", "C", "I am not sure", "cause_printer_connection" to 2)),
            question("q_printer_2", "What does the print queue show?",
                option("opt_printer_2a", "A", "Jobs are stuck or paused", "cause_printer_queue" to 5),
                option("opt_printer_2b", "B", "Printer shows Offline", "cause_printer_connection" to 4),
                option("opt_printer_2c", "C", "Queue is empty", "cause_printer_connection" to 3))
        ), listOf("print queue", "printer offline", "spooler", "won't print")
    ),
    Issue(
        "issue_usb_01", "cat_peripheral", "USB device not recognized",
        "A flash drive, webcam, keyboard, or other USB device is missing or reports an error.", "usb",
        listOf(
            question("q_usb_1", "Does the device work in another USB port or computer?",
                option("opt_usb_1a", "A", "It works in another port on this PC", "cause_usb_port" to 5),
                option("opt_usb_1b", "B", "It works on another computer only", "cause_usb_driver" to 5),
                option("opt_usb_1c", "C", "It works nowhere", "cause_usb_port" to 3)),
            question("q_usb_2", "What appears in Device Manager?",
                option("opt_usb_2a", "A", "Unknown USB Device or yellow warning icon", "cause_usb_driver" to 5),
                option("opt_usb_2b", "B", "Nothing changes when connected", "cause_usb_port" to 4),
                option("opt_usb_2c", "C", "The device appears and disappears", "cause_usb_port" to 3, "cause_usb_driver" to 3))
        ), listOf("unknown usb device", "flash drive missing", "usb port", "device manager")
    ),
    Issue(
        "issue_audio_01", "cat_peripheral", "No sound from speakers or headphones",
        "Windows shows the computer is running, but speakers or headphones produce no audio.", "volume_off",
        listOf(
            question("q_audio_1", "Where should the sound be coming from?",
                option("opt_audio_1a", "A", "Built-in speakers", "cause_audio_output" to 5),
                option("opt_audio_1b", "B", "Wired headphones or headset", "cause_audio_output" to 5),
                option("opt_audio_1c", "C", "Bluetooth headset or speaker", "cause_audio_output" to 3, "cause_audio_driver" to 3)),
            question("q_audio_2", "What do you see in the Windows sound controls?",
                option("opt_audio_2a", "A", "The wrong output device is selected", "cause_audio_output" to 5),
                option("opt_audio_2b", "B", "The correct device is selected but audio is muted", "cause_audio_output" to 4),
                option("opt_audio_2c", "C", "The device is missing or has a warning icon", "cause_audio_driver" to 5))
        ), listOf("no audio", "no sound", "headphones silent", "speakers not working", "muted")
    ),
    Issue(
        "issue_bluetooth_01", "cat_peripheral", "Bluetooth device won't connect",
        "A mouse, keyboard, headset, speaker, or other Bluetooth accessory will not pair or keeps disconnecting.", "bluetooth_disabled",
        listOf(
            question("q_bluetooth_1", "What happens when you try to connect?",
                option("opt_bluetooth_1a", "A", "The device does not appear in the list", "cause_bluetooth_adapter" to 5),
                option("opt_bluetooth_1b", "B", "It appears but pairing fails", "cause_bluetooth_pairing" to 5),
                option("opt_bluetooth_1c", "C", "It pairs, then disconnects", "cause_bluetooth_adapter" to 4)),
            question("q_bluetooth_2", "What has been checked already?",
                option("opt_bluetooth_2a", "A", "The accessory is not charged or may be connected elsewhere", "cause_bluetooth_pairing" to 5),
                option("opt_bluetooth_2b", "B", "Bluetooth was turned off and back on", "cause_bluetooth_adapter" to 3),
                option("opt_bluetooth_2c", "C", "The Bluetooth adapter has a warning in Device Manager", "cause_bluetooth_adapter" to 5))
        ), listOf("bluetooth pairing", "bluetooth not found", "wireless headset", "bluetooth mouse")
    ),
    Issue(
        "issue_keyboard_01", "cat_peripheral", "Keyboard keys not working or typing wrong",
        "Some keys do nothing, repeat unexpectedly, or type different characters than the keys pressed.", "keyboard",
        listOf(
            question("q_keyboard_1", "How does the keyboard problem appear?",
                option("opt_keyboard_1a", "A", "The whole keyboard is unresponsive", "cause_keyboard_connection" to 5),
                option("opt_keyboard_1b", "B", "Only a few keys do not work", "cause_keyboard_connection" to 4),
                option("opt_keyboard_1c", "C", "Keys type the wrong characters", "cause_keyboard_layout" to 5)),
            question("q_keyboard_2", "Does another keyboard behave the same way?",
                option("opt_keyboard_2a", "A", "No, another keyboard works normally", "cause_keyboard_connection" to 5),
                option("opt_keyboard_2b", "B", "Yes, both keyboards type wrong characters", "cause_keyboard_layout" to 5),
                option("opt_keyboard_2c", "C", "I have not tested another keyboard", "cause_keyboard_connection" to 2, "cause_keyboard_layout" to 2))
        ), listOf("keyboard not working", "keys not working", "wrong characters", "keyboard typing wrong")
    ),
    Issue(
        "issue_update_01", "cat_software", "Windows update failed or stuck",
        "Windows Update remains at one percentage, repeatedly fails, or rolls back after restarting.", "system_update_alt",
        listOf(
            question("q_update_1", "What is Windows Update doing?",
                option("opt_update_1a", "A", "It is stuck at the same percentage", "cause_update_cache" to 5),
                option("opt_update_1b", "B", "It shows an error code and fails", "cause_update_services" to 5),
                option("opt_update_1c", "C", "It installs then rolls back", "cause_update_services" to 4)),
            question("q_update_2", "What is the computer's current condition?",
                option("opt_update_2a", "A", "The system drive has very little free space", "cause_update_cache" to 5),
                option("opt_update_2b", "B", "The PC has been restarted and the error remains", "cause_update_services" to 4),
                option("opt_update_2c", "C", "A VPN, security tool, or cleanup utility is active", "cause_update_services" to 4))
        ), listOf("windows update failed", "update stuck", "update error", "windows update rollback")
    ),
    Issue(
        "issue_beep_01", "cat_other", "PC beeps and won't start",
        "The computer emits a repeating beep pattern or motherboard diagnostic light and does not boot.", "notifications_active",
        listOf(
            question("q_beep_1", "What pattern do you hear or see?",
                option("opt_beep_1a", "A", "Repeating short beeps or a DRAM light", "cause_beep_memory" to 5),
                option("opt_beep_1b", "B", "One long and two or three short beeps / VGA light", "cause_beep_graphics" to 5),
                option("opt_beep_1c", "C", "The pattern is different or undocumented", "cause_beep_memory" to 2, "cause_beep_graphics" to 2)),
            question("q_beep_2", "Did the PC recently move or receive new hardware?",
                option("opt_beep_2a", "A", "RAM was added or the PC was moved", "cause_beep_memory" to 4),
                option("opt_beep_2b", "B", "A graphics card was added or moved", "cause_beep_graphics" to 4),
                option("opt_beep_2c", "C", "No recent changes", "cause_beep_memory" to 2, "cause_beep_graphics" to 2))
        ), listOf("beep code", "dram light", "vga light", "post code", "motherboard light")
    )
)

internal fun expandedCauses(): List<Cause> = listOf(
    Cause("cause_battery_adapter", "issue_battery_01", "Charger, cable, or charging-port fault", "The adapter is not delivering stable power or the charging port has a loose connection.", listOf(
        step(1, "Verify the correct charger", "Compare the voltage and wattage on the adapter with the laptop specification. Use the original or a compatible USB-C PD charger."),
        step(2, "Power-reset the laptop", "Shut down, unplug the charger, hold the power button for 20 seconds, then reconnect and test."),
        step(3, "Inspect and cross-test", "Check the cable and port for damage. Test a known-good compatible charger if available.", "Stop if the port is hot, scorched, loose, or produces sparks.")),
        RepairDifficulty.BEGINNER, 15, listOf("Known-good compatible charger"), "The port is damaged, overheats, or charging still cuts out with a known-good adapter."),
    Cause("cause_battery_health", "issue_battery_01", "Battery wear or charge-limit setting", "The battery may be worn out, swollen, or intentionally limited by a battery-health mode.", listOf(
        step(1, "Check charge-limit settings", "Open the manufacturer's battery utility or BIOS and disable a 60%/80% conservation limit if full charging is needed."),
        step(2, "Generate a battery report", "Open Command Prompt and run `powercfg /batteryreport`, then compare Full Charge Capacity with Design Capacity."),
        step(3, "Replace an unsafe or worn battery", "Arrange the correct battery replacement if capacity is severely reduced or the case is bulging.", "Never press, puncture, bend, or continue charging a swollen battery.")),
        RepairDifficulty.INTERMEDIATE, 30, listOf("Device model number"), "The battery is swollen, leaking, unusually hot, or is not user-removable."),

    Cause("cause_drive_connection", "issue_drive_01", "Drive connection or hardware failure", "The SATA/NVMe drive is loose, unpowered, or beginning to fail.", listOf(
        step(1, "Check BIOS detection", "Open BIOS/UEFI storage information. If the drive is missing there, Windows settings are not the cause."),
        step(2, "Power off and reseat", "Unplug the PC, discharge static, then reseat the SATA data/power cable or NVMe module."),
        step(3, "Protect important data", "If the drive returns but clicks, freezes, or reports health warnings, copy essential files before further testing.", "Do not repeatedly power a clicking drive; professional recovery may be safer.")),
        RepairDifficulty.INTERMEDIATE, 30, listOf("Phillips screwdriver", "Known-good cable"), "The drive clicks, disappears repeatedly, or contains irreplaceable data."),
    Cause("cause_drive_partition", "issue_drive_01", "Drive is offline, unlettered, or unallocated", "Windows detects the hardware but the volume is offline, lacks a drive letter, or has no usable partition.", listOf(
        step(1, "Inspect Disk Management", "Right-click Start, open Disk Management, and locate the drive by its capacity."),
        step(2, "Bring an existing volume online", "If a healthy volume is Offline or lacks a letter, set it Online and assign an unused drive letter."),
        step(3, "Handle unallocated space carefully", "Create a new volume only when the drive is new or its data is not needed.", "Formatting or creating a volume can destroy recoverable data.")),
        RepairDifficulty.INTERMEDIATE, 20, emptyList(), "The drive previously held data but now appears RAW or unallocated."),

    Cause("cause_boot_recovery", "issue_boot_01", "Failed update, driver, or startup configuration", "A recent system change is preventing Windows from completing startup.", listOf(
        step(1, "Open Windows Recovery", "After repeated failed starts, choose Advanced options, Troubleshoot, then Startup Repair."),
        step(2, "Remove the recent change", "Use Uninstall Updates, System Restore, or Safe Mode to remove the newest driver or application."),
        step(3, "Back up before reset", "If recovery tools fail, back up accessible files before using Reset this PC or reinstalling Windows.")),
        RepairDifficulty.INTERMEDIATE, 45, listOf("Windows recovery access"), "The drive is not detected, is encrypted without a recovery key, or files are not backed up."),
    Cause("cause_boot_files", "issue_boot_01", "Boot files or boot-drive selection is damaged", "The firmware may target the wrong disk, or Windows boot records are unavailable.", listOf(
        step(1, "Confirm the boot drive", "In BIOS/UEFI, confirm the Windows drive is detected and Windows Boot Manager is first in boot order."),
        step(2, "Disconnect new external drives", "Remove USB storage and recently added secondary drives, then restart."),
        step(3, "Use Startup Repair", "Boot Windows recovery media and run Startup Repair before attempting command-line boot repair.")),
        RepairDifficulty.ADVANCED, 45, listOf("Windows installation USB"), "The drive is missing from BIOS, makes unusual sounds, or BitLocker recovery information is unavailable."),

    Cause("cause_app_cache", "issue_app_crash_01", "Damaged app cache or local settings", "Corrupt temporary files or preferences can make one application fail repeatedly.", listOf(
        step(1, "End and restart the app", "Close every instance in Task Manager, restart Windows, and launch the app again."),
        step(2, "Repair or reset", "Open Settings > Apps > Installed apps > Advanced options, then try Repair before Reset."),
        step(3, "Reinstall cleanly", "Export needed settings, uninstall the app, restart, and install the latest version from its official source.")),
        RepairDifficulty.BEGINNER, 20, emptyList(), "The app stores irreplaceable local data that has not been backed up."),
    Cause("cause_app_runtime", "issue_app_crash_01", "Incompatible update, driver, or runtime", "The app depends on a damaged runtime or conflicts with a recent system or graphics update.", listOf(
        step(1, "Install pending fixes", "Install stable Windows and application updates, then restart completely."),
        step(2, "Check Reliability Monitor", "Search for Reliability Monitor and open the failed-app event to identify the faulting module."),
        step(3, "Repair the dependency", "Update or roll back the graphics driver, then repair the runtime named in the crash details.")),
        RepairDifficulty.INTERMEDIATE, 30, emptyList(), "Crashes affect many applications or Windows itself."),

    Cause("cause_wifi_signal", "issue_wifi_drop_01", "Weak signal or radio interference", "Distance, walls, channel congestion, or nearby 2.4 GHz devices are disrupting Wi-Fi.", listOf(
        step(1, "Test beside the router", "Use the device near the router. A stable connection there confirms a coverage or interference problem."),
        step(2, "Choose the suitable band", "Use 5 GHz near the router for speed or 2.4 GHz farther away for range."),
        step(3, "Improve router placement", "Raise the router, keep it in an open central area, and move it away from microwaves and thick metal objects.")),
        RepairDifficulty.BEGINNER, 20, emptyList(), "Coverage remains poor throughout the property and requires access points or cabling."),
    Cause("cause_wifi_driver", "issue_wifi_drop_01", "Wi-Fi driver or power-management fault", "The wireless adapter driver or its sleep setting is dropping the connection.", listOf(
        step(1, "Forget and reconnect", "Forget the Wi-Fi network in Settings, reconnect, and enter the password again."),
        step(2, "Disable adapter power saving", "In Device Manager, open the Wi-Fi adapter Power Management tab and prevent Windows from turning it off."),
        step(3, "Install the manufacturer driver", "Download the correct Wi-Fi driver from the PC or adapter manufacturer and restart.")),
        RepairDifficulty.BEGINNER, 20, emptyList(), "The adapter disappears from Device Manager or has a persistent hardware error."),

    Cause("cause_ethernet_link", "issue_ethernet_01", "Cable, router port, or Ethernet port fault", "No physical link is being established between the computer and router.", listOf(
        step(1, "Reseat both ends", "Reconnect the Ethernet cable until each plug clicks and check for link lights."),
        step(2, "Cross-test cable and router port", "Try a known-good Cat5e/Cat6 cable and a different LAN port on the router."),
        step(3, "Inspect the PC port", "Check for bent contacts or looseness and test a USB Ethernet adapter if available.")),
        RepairDifficulty.BEGINNER, 15, listOf("Known-good Ethernet cable"), "The built-in port is physically damaged."),
    Cause("cause_ethernet_ip", "issue_ethernet_01", "Invalid IP or DHCP configuration", "The link works, but the computer did not receive a valid address from the router.", listOf(
        step(1, "Use automatic addressing", "Open the adapter's IPv4 properties and select automatic IP and DNS unless the network requires static values."),
        step(2, "Renew the address", "Open Command Prompt and run `ipconfig /release`, then `ipconfig /renew`."),
        step(3, "Restart the network path", "Restart the PC and router, then test again without a VPN or network bridge.")),
        RepairDifficulty.BEGINNER, 15, emptyList(), "A managed school or office network requires administrator-provided settings."),

    Cause("cause_slow_storage", "issue_slow_01", "Low free space or slow/failing storage", "A nearly full or unhealthy system drive delays startup, paging, and application loading.", listOf(
        step(1, "Free safe disk space", "Open Settings > System > Storage and remove temporary files. Keep at least 15% of the Windows drive free."),
        step(2, "Check drive health", "Review the drive manufacturer's health tool and Windows event logs for disk warnings."),
        step(3, "Plan an SSD upgrade", "If Windows runs from a healthy mechanical hard drive, moving to an SSD can substantially improve responsiveness.")),
        RepairDifficulty.BEGINNER, 30, emptyList(), "The drive reports a health warning, clicks, or contains data without a backup."),
    Cause("cause_startup_apps", "issue_slow_01", "Too many startup and background apps", "Multiple programs launch at sign-in and compete for memory, CPU, and disk access.", listOf(
        step(1, "Review Startup apps", "Open Task Manager > Startup apps and sort by Startup impact."),
        step(2, "Disable nonessential entries", "Disable launchers and helpers you recognize and do not need immediately. Leave security and hardware utilities enabled."),
        step(3, "Restart and measure", "Restart the PC and compare sign-in time before removing anything else.")),
        RepairDifficulty.BEGINNER, 15),

    Cause("cause_flicker_cable", "issue_flicker_01", "Loose display cable or panel connection", "A cable or laptop display hinge connection is intermittently losing signal.", listOf(
        step(1, "Reseat the external cable", "Turn off the display, reconnect both ends, and remove unnecessary adapters."),
        step(2, "Cross-test", "Try another cable, monitor, and output port one at a time."),
        step(3, "Check lid movement", "On a laptop, note whether changing the lid angle triggers flicker.", "Do not dismantle the laptop hinge or display without the correct service procedure.")),
        RepairDifficulty.BEGINNER, 20, listOf("Known-good display cable"), "Laptop lid movement triggers the fault or the connector is physically damaged."),
    Cause("cause_flicker_driver", "issue_flicker_01", "Graphics driver or refresh-rate mismatch", "The display mode or graphics driver is unstable.", listOf(
        step(1, "Reset the graphics driver", "Press Windows + Ctrl + Shift + B and observe whether the display stabilizes."),
        step(2, "Use a supported refresh rate", "Open Advanced display settings and select the monitor's supported native refresh rate."),
        step(3, "Update or roll back", "Install the PC/GPU manufacturer's stable driver, or roll back if the issue began immediately after an update.")),
        RepairDifficulty.BEGINNER, 20),

    Cause("cause_artifact_gpu", "issue_artifact_01", "GPU overheating, instability, or hardware failure", "Graphics memory or the GPU is producing corrupted output, especially under load.", listOf(
        step(1, "Return to stock settings", "Remove GPU overclocks or undervolts and restore default performance settings."),
        step(2, "Check cooling", "Power off and clear dust from GPU intakes and fans. Confirm fans spin under load."),
        step(3, "Cross-test output", "Test another display and cable. Artifacts visible in BIOS strongly suggest hardware failure.")),
        RepairDifficulty.INTERMEDIATE, 30, listOf("Compressed air"), "Artifacts appear in BIOS, the GPU overheats, or the system crashes under graphics load."),
    Cause("cause_artifact_monitor", "issue_artifact_01", "Monitor, panel, or display-cable fault", "The computer output may be correct while the display path distorts the image.", listOf(
        step(1, "Capture a screenshot", "View the screenshot on another device. A clean screenshot points to the monitor or cable."),
        step(2, "Replace the cable and input", "Use a known-good cable and a different input on the monitor."),
        step(3, "Test another display", "Connect the computer to another monitor or TV at its native resolution.")),
        RepairDifficulty.BEGINNER, 20, listOf("Known-good display cable", "Second display"), "The panel is cracked, smells burnt, or remains distorted with every source."),

    Cause("cause_printer_connection", "issue_printer_01", "Printer connection or address changed", "Windows is targeting an unavailable USB port or an old network address.", listOf(
        step(1, "Check the printer itself", "Clear paper or ink errors and print a built-in status page if the model supports it."),
        step(2, "Reconnect", "For USB, reconnect directly to the PC. For Wi-Fi, confirm the printer and PC use the same network."),
        step(3, "Remove and add the printer", "In Settings > Bluetooth & devices > Printers & scanners, remove the stale entry and add the printer again.")),
        RepairDifficulty.BEGINNER, 20),
    Cause("cause_printer_queue", "issue_printer_01", "Paused queue or stuck print spooler", "A corrupt print job is blocking every job behind it.", listOf(
        step(1, "Cancel queued jobs", "Open the printer queue, resume it if paused, and cancel all stuck jobs."),
        step(2, "Restart the Print Spooler", "Open Services, restart Print Spooler, then reopen the queue."),
        step(3, "Print a Windows test page", "Use Printer properties > Print Test Page before reopening the original document.")),
        RepairDifficulty.BEGINNER, 15),

    Cause("cause_usb_port", "issue_usb_01", "USB port, cable, or device hardware fault", "The device is not receiving stable power or data through the current port.", listOf(
        step(1, "Reconnect without a hub", "Connect directly to another USB port on the PC, avoiding unpowered hubs and front-panel extensions."),
        step(2, "Cross-test", "Try a known-good cable if detachable, then test the device on another computer."),
        step(3, "Inspect safely", "Power off before removing visible debris. Never force a plug into a damaged port.")),
        RepairDifficulty.BEGINNER, 15, listOf("Known-good USB cable"), "The port is bent, loose, scorched, or all ports fail."),
    Cause("cause_usb_driver", "issue_usb_01", "USB device driver or controller state is stuck", "Windows sees the device but cannot load a working driver.", listOf(
        step(1, "Restart fully", "Shut down the PC, disconnect the USB device for 30 seconds, start Windows, then reconnect."),
        step(2, "Reinstall the failed device", "In Device Manager, uninstall only the device with the warning icon, then scan for hardware changes."),
        step(3, "Install official drivers", "Install chipset and device drivers from the PC or device manufacturer.")),
        RepairDifficulty.BEGINNER, 20),

    Cause("cause_audio_output", "issue_audio_01", "Wrong output, mute, or cable connection", "Windows may be sending audio to another device, or the selected speakers/headphones are muted or disconnected.", listOf(
        step(1, "Select the correct output", "Open Settings > System > Sound and choose the speakers or headphones you want to use."),
        step(2, "Check volume and connections", "Raise the app and system volume, clear mute, and reseat the headphone or speaker cable."),
        step(3, "Test another output", "Try another port or a known-good headset to separate a device problem from a Windows setting.")),
        RepairDifficulty.BEGINNER, 15, listOf("Known-good headset"), "The port is loose, damaged, or produces crackling and heat."),
    Cause("cause_audio_driver", "issue_audio_01", "Audio driver or Windows audio service fault", "The audio device is missing, disabled, or its driver/service is not responding.", listOf(
        step(1, "Restart Windows audio", "Restart the PC and check that Windows Audio is running in Services."),
        step(2, "Check Device Manager", "Open Sound, video and game controllers and enable the device if disabled."),
        step(3, "Install the manufacturer driver", "Install the audio driver from the PC or motherboard manufacturer's support page.")),
        RepairDifficulty.BEGINNER, 20, emptyList(), "The audio device is missing after hardware damage or remains unavailable with the official driver."),

    Cause("cause_bluetooth_pairing", "issue_bluetooth_01", "Accessory is not ready to pair", "The accessory may be off, out of range, low on charge, or still paired with another device.", listOf(
        step(1, "Charge and enter pairing mode", "Charge the accessory, turn it off and on, then hold its pairing button until the indicator flashes."),
        step(2, "Remove the old pairing", "In Settings > Bluetooth & devices, remove the old entry and pair it again from nearby."),
        step(3, "Reduce interference", "Keep the accessory close to the PC and temporarily disconnect it from phones or tablets.")),
        RepairDifficulty.BEGINNER, 15),
    Cause("cause_bluetooth_adapter", "issue_bluetooth_01", "Bluetooth adapter, driver, or power state fault", "The PC adapter may be disabled, stuck after sleep, or using an incompatible driver.", listOf(
        step(1, "Toggle Bluetooth and restart", "Turn Bluetooth off and on, then restart the PC and accessory."),
        step(2, "Check the adapter", "In Device Manager, enable the Bluetooth adapter and install the PC manufacturer's driver if it has a warning."),
        step(3, "Disable power saving", "Open the adapter's Power Management settings and prevent Windows from turning it off to save power.")),
        RepairDifficulty.BEGINNER, 20, emptyList(), "The adapter is absent from Device Manager or fails with every Bluetooth device."),

    Cause("cause_keyboard_connection", "issue_keyboard_01", "Keyboard connection or hardware fault", "A USB/Bluetooth connection, cable, battery, or keyboard switch may be failing.", listOf(
        step(1, "Reconnect directly", "Reconnect the keyboard to another USB port without a hub, or charge it and re-enter Bluetooth pairing mode."),
        step(2, "Check for obstruction", "Power off, turn the keyboard over, and remove loose debris without forcing keys or liquid into the switches."),
        step(3, "Cross-test the keyboard", "Test the keyboard on another computer or test a known-good keyboard on this PC.")),
        RepairDifficulty.BEGINNER, 15, listOf("Known-good keyboard"), "The keyboard has liquid damage, a swollen battery, or fails on multiple computers."),
    Cause("cause_keyboard_layout", "issue_keyboard_01", "Wrong Windows keyboard layout or input mode", "Windows may be using another language/layout, Num Lock state, or an accessibility input setting.", listOf(
        step(1, "Check the input layout", "Select the intended language and keyboard layout from the taskbar input indicator."),
        step(2, "Test common toggles", "Check Num Lock, Caps Lock, and disable Filter Keys if keystrokes are delayed or ignored."),
        step(3, "Remove unused layouts", "Open Settings > Time & language > Language & region and remove layouts you do not use.")),
        RepairDifficulty.BEGINNER, 10),

    Cause("cause_update_cache", "issue_update_01", "Corrupt update cache or insufficient disk space", "Downloaded update files or a nearly full Windows drive can prevent the update from completing.", listOf(
        step(1, "Free drive space", "Open Settings > System > Storage and keep at least 15% of the Windows drive free."),
        step(2, "Run the troubleshooter", "Open Settings > System > Troubleshoot > Other troubleshooters and run Windows Update."),
        step(3, "Clear the update cache", "Stop Windows Update, clear the contents of C:\\Windows\\SoftwareDistribution\\Download, then restart the service.")),
        RepairDifficulty.INTERMEDIATE, 30, emptyList(), "The drive reports errors, is encrypted without a recovery key, or the update is a major version upgrade."),
    Cause("cause_update_services", "issue_update_01", "Windows Update service or conflicting software", "A stopped service, VPN, security tool, or damaged system component can interrupt installation.", listOf(
        step(1, "Disconnect conflicts", "Temporarily disconnect a VPN and close cleanup utilities; leave trusted antivirus protection enabled unless its vendor says otherwise."),
        step(2, "Check required services", "Confirm Windows Update, Background Intelligent Transfer Service, and Cryptographic Services are running."),
        step(3, "Repair system files", "Run `DISM /Online /Cleanup-Image /RestoreHealth`, then `sfc /scannow` in an elevated Command Prompt and restart.")),
        RepairDifficulty.INTERMEDIATE, 35, emptyList(), "The same error returns after repair, the PC is managed by an organization, or the update fails during rollback."),

    Cause("cause_beep_memory", "issue_beep_01", "RAM failed the startup self-test", "A loose, incompatible, or failed memory module is preventing POST.", listOf(
        step(1, "Identify the code", "Record the exact beep or diagnostic-light pattern and check the motherboard or PC service manual."),
        step(2, "Reseat memory", "Unplug power, discharge static, then reseat the RAM until both latches lock."),
        step(3, "Test one module", "If permitted by the service manual, test one RAM stick at a time in the recommended slot.")),
        RepairDifficulty.INTERMEDIATE, 30, listOf("Service manual", "Phillips screwdriver"), "The device is under warranty, soldered memory is involved, or the code persists with known-good RAM."),
    Cause("cause_beep_graphics", "issue_beep_01", "Graphics adapter failed the startup self-test", "The graphics card is unseated, lacks auxiliary power, or has failed.", listOf(
        step(1, "Confirm the code", "Match the beep/light pattern to the exact motherboard or PC model documentation."),
        step(2, "Check GPU power", "Unplug the PC and verify required PCIe power connectors are fully seated."),
        step(3, "Reseat or isolate", "Reseat the graphics card, or test integrated graphics only if the CPU and motherboard support it.")),
        RepairDifficulty.ADVANCED, 35, listOf("Service manual", "Phillips screwdriver"), "You are unsure about GPU power connectors, the card is under warranty, or the code remains."),

    Cause("cause_win_indexing", "issue_perf_01", "Windows indexing or background maintenance", "Search indexing, updates, or a security scan is consuming resources temporarily.", listOf(
        step(1, "Identify the process", "Open Task Manager, sort by CPU and Disk, and observe the top process for several minutes."),
        step(2, "Allow trusted maintenance to finish", "Keep the PC plugged in and idle while Windows Update, Search Indexer, or antivirus completes."),
        step(3, "Limit indexed locations", "Open Indexing Options and remove large folders that do not need instant search.")),
        RepairDifficulty.BEGINNER, 20),
    Cause("cause_browser_tabs", "issue_perf_01", "Browser tabs or extensions exhaust memory", "Many active pages or a faulty extension can consume most available RAM.", listOf(
        step(1, "Use the browser task manager", "In Chrome or Edge press Shift + Esc and identify the heaviest tab or extension."),
        step(2, "Close and suspend tabs", "Close unused tabs and enable the browser's memory-saver feature."),
        step(3, "Disable suspect extensions", "Turn off recently added extensions and re-enable them one at a time.")),
        RepairDifficulty.BEGINNER, 10),
    Cause("cause_malware", "issue_perf_01", "Unwanted or malicious background software", "An unknown process may be consuming resources or interfering with system tools.", listOf(
        step(1, "Disconnect if behavior is suspicious", "Disconnect from the network if files are being encrypted, accounts are opening, or pop-ups are uncontrollable."),
        step(2, "Run Microsoft Defender scans", "Run a Full scan, followed by Microsoft Defender Offline scan if the process returns."),
        step(3, "Secure accounts from a clean device", "If credentials may be exposed, change important passwords from another trusted device.")),
        RepairDifficulty.INTERMEDIATE, 60, emptyList(), "Ransomware, financial-account access, or persistent reinfection is suspected.")
)
