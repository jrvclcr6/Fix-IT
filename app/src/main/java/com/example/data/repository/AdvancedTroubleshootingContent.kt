package com.example.data.repository

import com.example.data.model.Cause
import com.example.data.model.Issue
import com.example.data.model.Option
import com.example.data.model.Question
import com.example.data.model.RepairDifficulty
import com.example.data.model.SolutionStep

private fun advancedOption(
    id: String,
    letter: String,
    text: String,
    vararg weights: Pair<String, Int>,
    dangerous: Boolean = false,
    warning: String? = null
) = Option(id, letter, text, dangerous, warning, mapOf(*weights))

private fun advancedQuestion(id: String, text: String, vararg options: Option) =
    Question(id, text, options.toList())

private fun advancedStep(number: Int, title: String, description: String, note: String? = null) =
    SolutionStep(number, title, description, tipOrWarning = note)

internal fun advancedIssues(): List<Issue> = listOf(
    Issue(
        "issue_restart_01", "cat_hardware", "PC restarts randomly without warning",
        "The computer suddenly restarts during normal use, under load, or shortly after Windows starts.", "restart_alt",
        listOf(
            advancedQuestion(
                "q_restart_1", "When do the restarts happen most often?",
                advancedOption("opt_restart_1a", "A", "During games or demanding work", "cause_restart_power" to 5),
                advancedOption("opt_restart_1b", "B", "Soon after Windows or a driver update", "cause_restart_windows" to 5),
                advancedOption("opt_restart_1c", "C", "At unpredictable times", "cause_restart_power" to 3, "cause_restart_windows" to 2)
            ),
            advancedQuestion(
                "q_restart_2", "What happens immediately before it restarts?",
                advancedOption("opt_restart_2a", "A", "Power cuts out with no error message", "cause_restart_power" to 5),
                advancedOption("opt_restart_2b", "B", "A blue screen or Windows error appears", "cause_restart_windows" to 5),
                advancedOption(
                    "opt_restart_2c", "C", "There is a burning smell, smoke, or sparking", "cause_restart_power" to 5,
                    dangerous = true,
                    warning = "STOP USING THE PC. Disconnect external power if it is safe to do so. Do not open a power-supply enclosure or continue testing equipment that smells burnt, smokes, or sparks."
                )
            )
        ),
        listOf("random restart", "unexpected reboot", "pc reboots", "power cuts out", "kernel power")
    ),
    Issue(
        "issue_fan_01", "cat_hardware", "Computer fan is loud, rattling, or grinding",
        "A desktop or laptop fan is unusually loud, vibrates, rattles, or makes a scraping sound.", "air",
        listOf(
            advancedQuestion(
                "q_fan_1", "How would you describe the sound?",
                advancedOption("opt_fan_1a", "A", "A steady rush of air that gets louder under load", "cause_fan_airflow" to 5),
                advancedOption("opt_fan_1b", "B", "Rattling, clicking, scraping, or grinding", "cause_fan_hardware" to 5),
                advancedOption("opt_fan_1c", "C", "The fan repeatedly speeds up and slows down", "cause_fan_airflow" to 4)
            ),
            advancedQuestion(
                "q_fan_2", "What can you observe around the vents?",
                advancedOption("opt_fan_2a", "A", "Dust buildup or blocked airflow", "cause_fan_airflow" to 5),
                advancedOption("opt_fan_2b", "B", "The sound changes when the device is moved", "cause_fan_hardware" to 5),
                advancedOption("opt_fan_2c", "C", "The vents look clear but the noise remains", "cause_fan_hardware" to 3, "cause_fan_airflow" to 2)
            )
        ),
        listOf("loud fan", "fan grinding", "fan rattling", "fan clicking", "noisy laptop")
    ),
    Issue(
        "issue_signin_01", "cat_software", "Windows sign-in or PIN is not working",
        "Windows rejects the password or PIN, reports that the PIN is unavailable, or loops back to the sign-in screen.", "password",
        listOf(
            advancedQuestion(
                "q_signin_1", "What message appears at sign-in?",
                advancedOption("opt_signin_1a", "A", "The password is incorrect", "cause_signin_account" to 5),
                advancedOption("opt_signin_1b", "B", "The PIN is unavailable or cannot be verified", "cause_signin_hello" to 5),
                advancedOption("opt_signin_1c", "C", "The screen returns to sign-in after accepting it", "cause_signin_hello" to 3, "cause_signin_account" to 2)
            ),
            advancedQuestion(
                "q_signin_2", "Which sign-in option can you still access?",
                advancedOption("opt_signin_2a", "A", "A Microsoft account password", "cause_signin_account" to 4),
                advancedOption("opt_signin_2b", "B", "Another administrator account", "cause_signin_hello" to 4),
                advancedOption("opt_signin_2c", "C", "No sign-in option works", "cause_signin_account" to 4, "cause_signin_hello" to 3)
            )
        ),
        listOf("pin unavailable", "forgot password", "windows hello", "cannot sign in", "login loop")
    ),
    Issue(
        "issue_malware_01", "cat_software", "Suspicious pop-ups or possible malware",
        "Unexpected ads, fake security alerts, browser redirects, or unknown programs keep appearing.", "security",
        listOf(
            advancedQuestion(
                "q_malware_1", "Where do the alerts appear?",
                advancedOption("opt_malware_1a", "A", "Only inside one web browser", "cause_malware_browser" to 5),
                advancedOption("opt_malware_1b", "B", "Across Windows, even when the browser is closed", "cause_malware_system" to 5),
                advancedOption("opt_malware_1c", "C", "I am not sure", "cause_malware_browser" to 2, "cause_malware_system" to 3)
            ),
            advancedQuestion(
                "q_malware_2", "What is the alert asking you to do?",
                advancedOption("opt_malware_2a", "A", "Allow notifications or install an extension", "cause_malware_browser" to 5),
                advancedOption("opt_malware_2b", "B", "Install unknown software or disable security", "cause_malware_system" to 5),
                advancedOption(
                    "opt_malware_2c", "C", "Call a number, pay money, or allow remote access", "cause_malware_system" to 5,
                    dangerous = true,
                    warning = "DO NOT CALL, PAY, OR ALLOW REMOTE ACCESS. Close the alert, disconnect the PC from the network if someone already connected, and contact a trusted technician or your financial provider if information was shared."
                )
            )
        ),
        listOf("virus pop up", "browser redirect", "fake security alert", "malware", "unwanted ads")
    ),
    Issue(
        "issue_wifi_missing_01", "cat_internet", "Wi-Fi network is missing or won't connect",
        "The expected wireless network is absent, or Windows repeatedly fails when connecting to it.", "wifi_find",
        listOf(
            advancedQuestion(
                "q_wifi_missing_1", "Can other devices see and use the same Wi-Fi network?",
                advancedOption("opt_wifi_missing_1a", "A", "Yes, only this PC has the problem", "cause_wifi_missing_pc" to 5),
                advancedOption("opt_wifi_missing_1b", "B", "No, the network is missing for every device", "cause_wifi_missing_router" to 5),
                advancedOption("opt_wifi_missing_1c", "C", "The network appears but this PC cannot join", "cause_wifi_missing_router" to 3, "cause_wifi_missing_pc" to 3)
            ),
            advancedQuestion(
                "q_wifi_missing_2", "What does Windows show for the wireless adapter?",
                advancedOption("opt_wifi_missing_2a", "A", "Wi-Fi is off or airplane mode is on", "cause_wifi_missing_pc" to 5),
                advancedOption("opt_wifi_missing_2b", "B", "The adapter is missing or has a warning", "cause_wifi_missing_pc" to 5),
                advancedOption("opt_wifi_missing_2c", "C", "The adapter looks normal", "cause_wifi_missing_router" to 4)
            )
        ),
        listOf("wifi missing", "network not showing", "cannot connect wifi", "wireless network absent", "airplane mode")
    ),
    Issue(
        "issue_internet_slow_01", "cat_internet", "Internet connection is unusually slow",
        "Websites, downloads, calls, or streams are much slower than expected on this computer.", "network_check",
        listOf(
            advancedQuestion(
                "q_internet_slow_1", "Which devices are slow on the same network?",
                advancedOption("opt_internet_slow_1a", "A", "Only this computer", "cause_internet_slow_pc" to 5),
                advancedOption("opt_internet_slow_1b", "B", "Every connected device", "cause_internet_slow_network" to 5),
                advancedOption("opt_internet_slow_1c", "C", "Only devices far from the router", "cause_internet_slow_network" to 5)
            ),
            advancedQuestion(
                "q_internet_slow_2", "What changes the speed?",
                advancedOption("opt_internet_slow_2a", "A", "Moving near the router improves it", "cause_internet_slow_network" to 5),
                advancedOption("opt_internet_slow_2b", "B", "Closing downloads, cloud sync, or VPN improves it", "cause_internet_slow_pc" to 5),
                advancedOption("opt_internet_slow_2c", "C", "Nothing noticeably changes it", "cause_internet_slow_network" to 3, "cause_internet_slow_pc" to 2)
            )
        ),
        listOf("slow internet", "slow download", "buffering", "high latency", "slow wifi")
    ),
    Issue(
        "issue_disk_usage_01", "cat_performance", "Disk usage stays at 100%",
        "Task Manager shows the system drive at or near 100% active time and the computer becomes unresponsive.", "data_usage",
        listOf(
            advancedQuestion(
                "q_disk_usage_1", "Which process is using the disk most?",
                advancedOption("opt_disk_usage_1a", "A", "Windows Update, search, antivirus, or another known task", "cause_disk_background" to 5),
                advancedOption("opt_disk_usage_1b", "B", "System or an unknown process with very low transfer speed", "cause_disk_health" to 5),
                advancedOption("opt_disk_usage_1c", "C", "No single process stands out", "cause_disk_health" to 3, "cause_disk_background" to 2)
            ),
            advancedQuestion(
                "q_disk_usage_2", "What is the drive's condition?",
                advancedOption("opt_disk_usage_2a", "A", "It has very little free space", "cause_disk_health" to 5),
                advancedOption("opt_disk_usage_2b", "B", "Usage settles after updates or a scan finishes", "cause_disk_background" to 5),
                advancedOption("opt_disk_usage_2c", "C", "It clicks, reports errors, or remains slow after restart", "cause_disk_health" to 5)
            )
        ),
        listOf("100 disk", "disk active time", "task manager disk", "system slow disk", "drive usage")
    ),
    Issue(
        "issue_gaming_01", "cat_performance", "Games stutter or have low FPS",
        "Games pause, hitch, or run at a much lower frame rate than expected even though Windows still responds.", "sports_esports",
        listOf(
            advancedQuestion(
                "q_gaming_1", "When does performance become worse?",
                advancedOption("opt_gaming_1a", "A", "After several minutes of play", "cause_gaming_thermal" to 5),
                advancedOption("opt_gaming_1b", "B", "Immediately after a game or graphics update", "cause_gaming_software" to 5),
                advancedOption("opt_gaming_1c", "C", "When many apps are open", "cause_gaming_software" to 4)
            ),
            advancedQuestion(
                "q_gaming_2", "What do monitoring tools or power settings show?",
                advancedOption("opt_gaming_2a", "A", "High temperatures or clock speeds dropping", "cause_gaming_thermal" to 5),
                advancedOption("opt_gaming_2b", "B", "The game uses integrated graphics or a power-saving mode", "cause_gaming_software" to 5),
                advancedOption("opt_gaming_2c", "C", "Nothing unusual is visible", "cause_gaming_thermal" to 2, "cause_gaming_software" to 3)
            )
        ),
        listOf("low fps", "game stutter", "frame drops", "gaming lag", "integrated graphics")
    ),
    Issue(
        "issue_scaling_01", "cat_display", "Display looks blurry, stretched, or incorrectly scaled",
        "Text and apps appear fuzzy, oversized, cropped, stretched, or shown at the wrong resolution.", "aspect_ratio",
        listOf(
            advancedQuestion(
                "q_scaling_1", "Which parts of the display look wrong?",
                advancedOption("opt_scaling_1a", "A", "Everything is stretched, cropped, or the wrong size", "cause_scaling_settings" to 5),
                advancedOption("opt_scaling_1b", "B", "Only certain apps or text look blurry", "cause_scaling_apps" to 5),
                advancedOption("opt_scaling_1c", "C", "The image has borders or extends off-screen", "cause_scaling_settings" to 5)
            ),
            advancedQuestion(
                "q_scaling_2", "What changed recently?",
                advancedOption("opt_scaling_2a", "A", "A monitor, cable, dock, or TV was connected", "cause_scaling_settings" to 5),
                advancedOption("opt_scaling_2b", "B", "A graphics driver or app was updated", "cause_scaling_apps" to 5),
                advancedOption("opt_scaling_2c", "C", "Nothing that I know of", "cause_scaling_settings" to 3, "cause_scaling_apps" to 2)
            )
        ),
        listOf("blurry screen", "wrong resolution", "display scaling", "overscan", "stretched display")
    ),
    Issue(
        "issue_mouse_01", "cat_peripheral", "Mouse or touchpad is not working correctly",
        "The pointer does not move, jumps around, clicks unexpectedly, or the touchpad is disabled.", "mouse",
        listOf(
            advancedQuestion(
                "q_mouse_1", "Which pointing device has the problem?",
                advancedOption("opt_mouse_1a", "A", "A USB or wireless mouse", "cause_mouse_connection" to 5),
                advancedOption("opt_mouse_1b", "B", "The laptop touchpad", "cause_mouse_touchpad" to 5),
                advancedOption("opt_mouse_1c", "C", "Both mouse and touchpad", "cause_mouse_touchpad" to 3, "cause_mouse_connection" to 3)
            ),
            advancedQuestion(
                "q_mouse_2", "What happens after reconnecting or restarting?",
                advancedOption("opt_mouse_2a", "A", "Another port or new battery fixes it", "cause_mouse_connection" to 5),
                advancedOption("opt_mouse_2b", "B", "The touchpad remains disabled or missing", "cause_mouse_touchpad" to 5),
                advancedOption("opt_mouse_2c", "C", "The pointer still jumps or clicks by itself", "cause_mouse_connection" to 2, "cause_mouse_touchpad" to 4)
            )
        ),
        listOf("mouse not working", "touchpad disabled", "cursor jumping", "pointer missing", "mouse clicks itself")
    ),
    Issue(
        "issue_webcam_01", "cat_peripheral", "Webcam is missing or shows no video",
        "A meeting or camera app cannot find the webcam, shows a black image, or reports that access is blocked.", "videocam_off",
        listOf(
            advancedQuestion(
                "q_webcam_1", "What does the camera app show?",
                advancedOption("opt_webcam_1a", "A", "Camera access is denied or blocked", "cause_webcam_privacy" to 5),
                advancedOption("opt_webcam_1b", "B", "No camera is detected", "cause_webcam_driver" to 5),
                advancedOption("opt_webcam_1c", "C", "A black image or the wrong camera", "cause_webcam_privacy" to 3, "cause_webcam_driver" to 3)
            ),
            advancedQuestion(
                "q_webcam_2", "Which checks have you made?",
                advancedOption("opt_webcam_2a", "A", "The privacy shutter or camera key was closed", "cause_webcam_privacy" to 5),
                advancedOption("opt_webcam_2b", "B", "Device Manager shows a warning or no camera", "cause_webcam_driver" to 5),
                advancedOption("opt_webcam_2c", "C", "Another app may already be using it", "cause_webcam_privacy" to 4)
            )
        ),
        listOf("camera not found", "webcam black", "camera privacy", "meeting camera", "webcam missing")
    ),
    Issue(
        "issue_cmos_01", "cat_other", "Date, time, or BIOS settings keep resetting",
        "The clock becomes incorrect after shutdown, or firmware settings and boot order repeatedly return to defaults.", "schedule",
        listOf(
            advancedQuestion(
                "q_cmos_1", "When do the settings reset?",
                advancedOption("opt_cmos_1a", "A", "After the PC is unplugged or fully powered off", "cause_cmos_battery" to 5),
                advancedOption("opt_cmos_1b", "B", "After a firmware update or failed boot", "cause_cmos_firmware" to 5),
                advancedOption("opt_cmos_1c", "C", "At random times", "cause_cmos_battery" to 3, "cause_cmos_firmware" to 2)
            ),
            advancedQuestion(
                "q_cmos_2", "What else appears during startup?",
                advancedOption("opt_cmos_2a", "A", "A CMOS, checksum, or time-and-date warning", "cause_cmos_battery" to 5),
                advancedOption("opt_cmos_2b", "B", "A firmware recovery or defaults-loaded message", "cause_cmos_firmware" to 5),
                advancedOption("opt_cmos_2c", "C", "No warning, only an incorrect Windows clock", "cause_cmos_battery" to 3, "cause_cmos_firmware" to 3)
            )
        ),
        listOf("cmos battery", "clock resets", "bios resets", "checksum error", "date time wrong")
    )
)

internal fun advancedCauses(): List<Cause> = listOf(
    Cause("cause_restart_power", "issue_restart_01", "Unstable power delivery or hardware protection", "A failing adapter, power supply, loose connection, or protective shutdown can cut power without a Windows error.", listOf(
        advancedStep(1, "Remove external variables", "Shut down, disconnect unnecessary USB devices, and connect directly to a known-good wall outlet or compatible adapter."),
        advancedStep(2, "Check visible connections", "With power disconnected, reseat only user-serviceable external power and battery connections; never open a power-supply enclosure."),
        advancedStep(3, "Compare light and heavy use", "Restart and test light use before a demanding task. Record whether load reliably triggers the restart.")),
        RepairDifficulty.INTERMEDIATE, 25, listOf("Known-good compatible power adapter, if available"), "There is smoke, sparking, a burning smell, visible damage, or restarts continue with a known-good power source."),
    Cause("cause_restart_windows", "issue_restart_01", "Windows, driver, or memory instability", "A recent driver, update, memory error, or automatic restart after a system failure can reboot Windows.", listOf(
        advancedStep(1, "Review recent changes", "Check Windows Update history and recently installed drivers or hardware around the time the restarts began."),
        advancedStep(2, "Reveal the failure", "Open Advanced system settings and disable automatic restart after system failure so an error can be recorded."),
        advancedStep(3, "Run built-in checks", "Run Windows Memory Diagnostic and install stable chipset and graphics drivers from the PC manufacturer.")),
        RepairDifficulty.INTERMEDIATE, 35, emptyList(), "Memory errors repeat, Windows cannot remain stable long enough to back up data, or the PC is organization-managed."),

    Cause("cause_fan_airflow", "issue_fan_01", "Restricted airflow or sustained heat", "Dust, blocked vents, background load, or an unsuitable surface can force fans to run at high speed.", listOf(
        advancedStep(1, "Move to a hard surface", "Place the device on a firm, flat surface and keep every intake and exhaust vent unobstructed."),
        advancedStep(2, "Remove loose dust safely", "Shut down and disconnect power. Use short bursts of compressed air from outside while preventing accessible fan blades from overspinning."),
        advancedStep(3, "Check background load", "After restart, use Task Manager to identify sustained CPU or GPU activity and allow legitimate updates or scans to finish.")),
        RepairDifficulty.BEGINNER, 20, listOf("Compressed air"), "The case becomes dangerously hot, the fan stops spinning, or the system also shuts down from heat."),
    Cause("cause_fan_hardware", "issue_fan_01", "Worn fan bearing or physical obstruction", "A cable, broken blade, loose mount, or worn bearing can cause grinding, scraping, or vibration.", listOf(
        advancedStep(1, "Power off immediately", "Shut down and disconnect power before inspecting any accessible vent or removable service panel."),
        advancedStep(2, "Locate the noise", "Without touching components, identify which vent or fan area produces the noise and check for a visible loose cable or debris."),
        advancedStep(3, "Arrange fan replacement", "Use the exact device service manual or a qualified technician to replace the correct fan assembly.")),
        RepairDifficulty.ADVANCED, 40, listOf("Device service manual"), "The device is under warranty, requires major disassembly, has a swollen battery, or the fan is seized."),

    Cause("cause_signin_account", "issue_signin_01", "Account password, keyboard layout, or connectivity mismatch", "The entered credentials may use the wrong keyboard layout, an old cached password, or an account that requires recovery.", listOf(
        advancedStep(1, "Verify the input", "Use the password reveal button, check Caps Lock and the keyboard layout, then try the password rather than the PIN."),
        advancedStep(2, "Check sign-in connectivity", "From the sign-in screen, connect to a trusted network if the Microsoft account password changed recently."),
        advancedStep(3, "Use official recovery", "Choose the account recovery option from another trusted device and follow the provider's identity checks.")),
        RepairDifficulty.BEGINNER, 20, emptyList(), "You cannot verify account ownership, recovery details are unavailable, or the PC belongs to an organization."),
    Cause("cause_signin_hello", "issue_signin_01", "Windows Hello or local sign-in component fault", "The saved PIN or biometric credential can become unavailable after security, firmware, or account changes.", listOf(
        advancedStep(1, "Switch sign-in method", "Select Sign-in options and use the account password or another configured method."),
        advancedStep(2, "Reset the PIN", "After signing in, open Accounts > Sign-in options and use the supported PIN reset flow."),
        advancedStep(3, "Repair from a safe account", "If another administrator account works, install pending Windows updates and verify the affected user profile.")),
        RepairDifficulty.INTERMEDIATE, 30, emptyList(), "BitLocker requests an unavailable recovery key, no administrator can sign in, or organization policy controls Windows Hello."),

    Cause("cause_malware_browser", "issue_malware_01", "Abusive browser notifications or extension", "A website permission, extension, or altered browser setting can generate convincing fake alerts and redirects.", listOf(
        advancedStep(1, "Close the alert safely", "Do not click inside the pop-up. Close the tab or browser from Task Manager if the page will not close."),
        advancedStep(2, "Remove unwanted permissions", "Review the browser's site notification permissions and extensions, removing entries you do not recognize."),
        advancedStep(3, "Reset and scan", "Reset affected browser settings, update the browser, and run a full scan with Windows Security.")),
        RepairDifficulty.BEGINNER, 25, emptyList(), "The browser is organization-managed, account credentials were entered into a suspicious page, or redirects affect every browser."),
    Cause("cause_malware_system", "issue_malware_01", "Potentially unwanted or malicious software", "Unknown software may be starting with Windows, changing security settings, or displaying alerts outside the browser.", listOf(
        advancedStep(1, "Disconnect if actively compromised", "If remote control or account theft is suspected, disconnect Wi-Fi or Ethernet without shutting down evidence needed by an organization."),
        advancedStep(2, "Run trusted security scans", "Open Windows Security, update protection definitions, then run a full scan and Microsoft Defender Offline scan."),
        advancedStep(3, "Remove recent unknown software", "Review installed apps and startup items by date. Remove only software you can identify as unwanted, then change exposed passwords from a clean device.")),
        RepairDifficulty.INTERMEDIATE, 60, emptyList(), "Security tools are disabled, ransomware or financial loss is suspected, business data is involved, or infection returns after an offline scan."),

    Cause("cause_wifi_missing_pc", "issue_wifi_missing_01", "Wireless adapter is disabled or its driver is unavailable", "Airplane mode, a hardware key, power state, or driver fault can hide Wi-Fi from Windows.", listOf(
        advancedStep(1, "Enable the radio", "Turn off airplane mode, turn Wi-Fi on, and check the laptop's wireless key or switch if it has one."),
        advancedStep(2, "Restart the adapter", "In Device Manager, enable the wireless adapter, then disable and re-enable it if it is present without an error."),
        advancedStep(3, "Install the official driver", "Restart the PC and install the wireless driver from the PC manufacturer's support page using another connection if necessary.")),
        RepairDifficulty.BEGINNER, 20, emptyList(), "The adapter is absent after restart, shows a persistent hardware error, or the device is organization-managed."),
    Cause("cause_wifi_missing_router", "issue_wifi_missing_01", "Router broadcast, password, or compatibility problem", "The router may not be broadcasting, may use changed credentials, or may be using a band/security mode the device cannot join.", listOf(
        advancedStep(1, "Restart network equipment", "Unplug the router or access point for 30 seconds, reconnect it, and wait until its status lights stabilize."),
        advancedStep(2, "Forget and rejoin", "If the network appears, forget the saved profile and reconnect using the current password."),
        advancedStep(3, "Check router compatibility", "From the router's documented settings, confirm the network name is broadcast and a supported security mode and Wi-Fi band are enabled.")),
        RepairDifficulty.INTERMEDIATE, 25, emptyList(), "Changing router settings could disrupt other users, the router belongs to an ISP or organization, or no device can connect after restart."),

    Cause("cause_internet_slow_pc", "issue_internet_slow_01", "This PC is consuming or filtering bandwidth", "Cloud sync, downloads, VPN software, updates, or an unhealthy adapter can slow one computer.", listOf(
        advancedStep(1, "Pause known transfers", "Pause large downloads, cloud synchronization, game updates, and streaming, then compare performance."),
        advancedStep(2, "Test without optional routing", "Temporarily disconnect a personal VPN or proxy and test a trusted website; restore required work or school settings afterward."),
        advancedStep(3, "Refresh the adapter", "Restart the PC, update the network driver, and compare Wi-Fi with Ethernet when available.")),
        RepairDifficulty.BEGINNER, 20, emptyList(), "The computer is organization-managed, security software requires the VPN, or the adapter repeatedly disconnects or overheats."),
    Cause("cause_internet_slow_network", "issue_internet_slow_01", "Weak Wi-Fi, network congestion, or provider slowdown", "Distance, interference, busy network equipment, or the internet service can affect every device.", listOf(
        advancedStep(1, "Test near the router", "Move a device near the router and compare it with a wired connection if available."),
        advancedStep(2, "Restart and reduce congestion", "Restart the router, pause unnecessary high-bandwidth activity, and retest after status lights stabilize."),
        advancedStep(3, "Compare with the service plan", "Use the provider's supported test method on a wired device and record results at different times before contacting support.")),
        RepairDifficulty.BEGINNER, 25, listOf("Ethernet cable, if available"), "The router is unusually hot, has damaged cables, repeatedly restarts, or service remains slow across devices and wired tests."),

    Cause("cause_disk_background", "issue_disk_usage_01", "Windows maintenance or background software is busy", "Updates, indexing, antivirus scanning, synchronization, or paging can temporarily keep the drive active.", listOf(
        advancedStep(1, "Identify the active task", "In Task Manager, sort Processes by Disk and note the exact process and transfer rate."),
        advancedStep(2, "Allow safe maintenance to finish", "Keep the PC powered and let trusted Windows updates, search indexing, or security scans complete."),
        advancedStep(3, "Reduce repeat load", "Disable unnecessary startup apps and schedule large sync or scan tasks for a convenient time.")),
        RepairDifficulty.BEGINNER, 25, emptyList(), "An unknown process dominates disk use, security tools report a threat, or the system cannot stay responsive long enough to back up data."),
    Cause("cause_disk_health", "issue_disk_usage_01", "Low free space or deteriorating storage", "A nearly full, error-prone, or failing drive can remain at 100% active time even while transferring little data.", listOf(
        advancedStep(1, "Protect important files", "Back up irreplaceable files before intensive tests if the drive clicks, disappears, or reports health warnings."),
        advancedStep(2, "Free safe space", "Use Storage settings to remove temporary files and keep at least 15% of the system drive free."),
        advancedStep(3, "Check reported health", "Run the PC or drive manufacturer's diagnostics and review Windows drive error notifications.")),
        RepairDifficulty.INTERMEDIATE, 40, listOf("Backup storage"), "The drive clicks, disappears, fails diagnostics, contains critical unbacked-up data, or BitLocker recovery information is unavailable."),

    Cause("cause_gaming_thermal", "issue_gaming_01", "Thermal or power throttling", "High temperature, blocked airflow, or a low-power profile can reduce CPU or GPU speed during play.", listOf(
        advancedStep(1, "Use a suitable power mode", "Connect the correct adapter for a laptop and select a balanced or performance mode supported by the manufacturer."),
        advancedStep(2, "Restore airflow", "Use a hard surface, clear exterior vents, and stop if temperatures or case heat become unsafe."),
        advancedStep(3, "Compare a cold start", "After the device cools, test the same game briefly and note whether performance falls as temperature rises.")),
        RepairDifficulty.BEGINNER, 25, emptyList(), "The system shuts down, the battery is swollen, fans do not spin, or temperature remains unsafe after airflow is restored."),
    Cause("cause_gaming_software", "issue_gaming_01", "Graphics selection, driver, or workload mismatch", "The game may use integrated graphics, unsuitable settings, a problematic driver, or too many background resources.", listOf(
        advancedStep(1, "Close competing workloads", "Close unnecessary browsers, overlays, launchers, recording tools, and background downloads."),
        advancedStep(2, "Select the intended GPU", "Use Windows Graphics settings or the PC manufacturer's control panel to assign the high-performance GPU when one is available."),
        advancedStep(3, "Stabilize settings and drivers", "Use the game's recommended preset and install a stable graphics driver from the PC or GPU manufacturer.")),
        RepairDifficulty.BEGINNER, 30, emptyList(), "Artifacts, crashes, or driver resets affect multiple games, or the GPU is not detected correctly."),

    Cause("cause_scaling_settings", "issue_scaling_01", "Incorrect resolution, scaling, or display overscan", "Windows or the external display may be using a non-native resolution, unsuitable scale, or TV overscan mode.", listOf(
        advancedStep(1, "Use the recommended mode", "Open Display settings and select the resolution and scale marked Recommended for the active display."),
        advancedStep(2, "Check each monitor", "Select each display separately and verify orientation, refresh rate, and arrangement."),
        advancedStep(3, "Correct external display sizing", "For a TV or monitor, use its documented aspect or screen-fit mode and avoid zoom or overscan.")),
        RepairDifficulty.BEGINNER, 15, emptyList(), "The recommended resolution is missing, the display repeatedly disconnects, or changing modes leaves the screen unreadable."),
    Cause("cause_scaling_apps", "issue_scaling_01", "Application DPI or graphics driver mismatch", "Older apps or a changed graphics driver can render some content blurry even when Windows resolution is correct.", listOf(
        advancedStep(1, "Restart affected apps", "Sign out and back in after changing display scale, then reopen the affected applications."),
        advancedStep(2, "Update the app", "Install the application's current supported version and check its own zoom or display settings."),
        advancedStep(3, "Repair the graphics driver", "Install a stable graphics driver from the PC or GPU manufacturer and restart.")),
        RepairDifficulty.BEGINNER, 25, emptyList(), "Blurriness is accompanied by artifacts, flicker, or missing display modes across Windows."),

    Cause("cause_mouse_connection", "issue_mouse_01", "Mouse power, surface, receiver, or connection fault", "A low battery, dirty sensor, unsuitable surface, loose cable, or failing USB receiver can interrupt pointer input.", listOf(
        advancedStep(1, "Restore power and connection", "Replace or charge the battery, reconnect the cable or receiver directly, and try another USB port."),
        advancedStep(2, "Clean and test the sensor", "Power the mouse off, gently clean the sensor, and test on a plain non-reflective surface."),
        advancedStep(3, "Cross-test", "Try the mouse on another computer or use a known-good mouse on this PC.")),
        RepairDifficulty.BEGINNER, 15, listOf("Known-good mouse, if available"), "The device has liquid damage, a swollen battery, a damaged cable, or fails on multiple computers."),
    Cause("cause_mouse_touchpad", "issue_mouse_01", "Touchpad setting, gesture, or driver fault", "A function key, touchpad setting, external-mouse rule, or driver can disable or destabilize the touchpad.", listOf(
        advancedStep(1, "Re-enable the touchpad", "Use the laptop's touchpad function key, then check Bluetooth & devices > Touchpad in Windows settings."),
        advancedStep(2, "Check external-mouse behavior", "Disconnect external pointing devices and disable any setting that turns the touchpad off when a mouse is connected."),
        advancedStep(3, "Install the laptop driver", "Restart and install the touchpad or input driver from the laptop manufacturer.")),
        RepairDifficulty.BEGINNER, 20, emptyList(), "The touchpad is lifting, the palm rest is bulging, liquid entered the laptop, or the device remains absent from firmware and Windows."),

    Cause("cause_webcam_privacy", "issue_webcam_01", "Camera privacy control or application conflict", "A shutter, keyboard camera key, Windows permission, or another application can block video.", listOf(
        advancedStep(1, "Open physical privacy controls", "Open the camera shutter and use the laptop's camera function key if one is present."),
        advancedStep(2, "Allow camera access", "In Privacy & security > Camera, allow camera access and permission for the intended desktop or Store app."),
        advancedStep(3, "Close competing apps", "Quit meeting, recording, and browser apps, then test with the built-in Camera app.")),
        RepairDifficulty.BEGINNER, 15, emptyList(), "The camera activates unexpectedly, privacy controls do not work, or organization policy blocks access."),
    Cause("cause_webcam_driver", "issue_webcam_01", "Webcam driver, USB link, or camera hardware fault", "Windows may not be loading the camera, or an external webcam may have an unstable cable or port.", listOf(
        advancedStep(1, "Reconnect and restart", "Reconnect an external webcam directly to another USB port, or restart the laptop for an internal camera."),
        advancedStep(2, "Check Device Manager", "Enable the camera if disabled and note any error before updating or reinstalling the manufacturer's driver."),
        advancedStep(3, "Cross-test", "Test an external webcam on another computer, or test the laptop camera in the manufacturer's diagnostics.")),
        RepairDifficulty.BEGINNER, 25, emptyList(), "The camera is absent from firmware diagnostics, the cable or display hinge is damaged, or the device remains unavailable with the official driver."),

    Cause("cause_cmos_battery", "issue_cmos_01", "Weak real-time-clock or CMOS battery", "The small battery that preserves time and firmware settings may no longer hold charge when external power is removed.", listOf(
        advancedStep(1, "Confirm the pattern", "Set the correct time and firmware options, shut down, remove external power, then check whether they reset again."),
        advancedStep(2, "Identify the service procedure", "Use the exact PC or motherboard service manual to locate the supported RTC or CMOS battery type."),
        advancedStep(3, "Replace only if serviceable", "With power disconnected and static precautions in place, replace a user-serviceable battery with the specified type and restore firmware settings.")),
        RepairDifficulty.INTERMEDIATE, 35, listOf("Exact replacement battery", "Device service manual"), "The battery is soldered or inaccessible, the device is under warranty, BitLocker recovery information is unavailable, or firmware settings are unfamiliar."),
    Cause("cause_cmos_firmware", "issue_cmos_01", "Firmware settings, update, or board fault", "A failed firmware update, automatic recovery, unstable settings, or motherboard fault can repeatedly load defaults.", listOf(
        advancedStep(1, "Record current settings", "Photograph important firmware screens and make sure any BitLocker recovery key is available before changing firmware."),
        advancedStep(2, "Load stable defaults once", "Use the firmware's documented default-settings option, then correct only the date, boot order, and required manufacturer settings."),
        advancedStep(3, "Use official firmware support", "Check the PC or motherboard manufacturer's instructions for a supported firmware recovery or update.")),
        RepairDifficulty.ADVANCED, 45, emptyList(), "Firmware recovery repeats, the board loses settings with a known-good battery, the PC is organization-managed, or BitLocker recovery is triggered.")
)
