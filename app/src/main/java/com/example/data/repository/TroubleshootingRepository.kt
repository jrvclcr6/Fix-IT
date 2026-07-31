package com.example.data.repository

import com.example.data.model.Category
import com.example.data.model.Cause
import com.example.data.model.DiagnosticResult
import com.example.data.model.Issue
import com.example.data.model.Option
import com.example.data.model.Question
import com.example.data.model.SolutionStep

class TroubleshootingRepository {

    val categories: List<Category> = listOf(
        Category("cat_hardware", "Hardware Problems", "Power, motherboard, RAM, PSU, and internal components", "desktop_windows", 5),
        Category("cat_software", "Software Problems", "Operating system crashes, boot errors, BSOD, and drivers", "code", 3),
        Category("cat_internet", "Internet Problems", "Wi-Fi connectivity, DNS errors, router, and ethernet", "wifi", 3),
        Category("cat_performance", "Performance Problems", "System slowdowns, 100% CPU/RAM usage, and gaming lag", "speed", 2),
        Category("cat_display", "Display Problems", "No signal, screen flickering, black screen, and artifacts", "monitor", 2),
        Category("cat_peripheral", "Peripheral Problems", "Printers, Bluetooth, webcams, and external storage", "print", 2),
        Category("cat_other", "Other Problems", "Liquid spills, beep codes, CMOS battery, and safety hazards", "warning", 2)
    )

    private val issuesMap: Map<String, Issue> = (listOf(
        // 1. Hardware: Computer won't turn on
        Issue(
            id = "issue_pwr_01",
            categoryId = "cat_hardware",
            title = "Computer won't turn on",
            description = "No lights, no fan spinning, and no sound when pressing the power button.",
            iconName = "power_settings_new",
            questions = listOf(
                Question(
                    id = "q_pwr_1",
                    text = "When did the problem start?",
                    options = listOf(
                        Option("opt_pwr_1a", "A", "After I turned it on or plugged it in", causeWeights = mapOf("cause_psu" to 3, "cause_outlet" to 2)),
                        Option("opt_pwr_1b", "B", "After using it for a while / sudden shutdown", causeWeights = mapOf("cause_psu" to 4, "cause_ram" to 1)),
                        Option("opt_pwr_1c", "C", "After a recent change (new hardware/power outage)", causeWeights = mapOf("cause_psu" to 4, "cause_ram" to 2)),
                        Option("opt_pwr_1d", "D", "After a liquid spill or physical drop", isDangerous = true, dangerWarning = "CRITICAL SAFETY ALERT: Potential internal electrical short circuit! Unplug immediately and DO NOT press power.", causeWeights = mapOf("cause_psu" to 5))
                    )
                ),
                Question(
                    id = "q_pwr_2",
                    text = "What happens when you press the power button?",
                    options = listOf(
                        Option("opt_pwr_2a", "A", "Nothing happens (completely silent, no lights)", causeWeights = mapOf("cause_psu" to 4, "cause_outlet" to 3)),
                        Option("opt_pwr_2b", "B", "Power light turns on briefly then shuts off instantly", causeWeights = mapOf("cause_psu" to 4, "cause_ram" to 2)),
                        Option("opt_pwr_2c", "C", "Fans spin but no display on screen", causeWeights = mapOf("cause_ram" to 5)),
                        Option("opt_pwr_2d", "D", "Sparks, smoke, or a burning smell was noticed", isDangerous = true, dangerWarning = "FIRE & HAZARD WARNING: Electrical damage/burning detected! Disconnect power from wall outlet immediately.", causeWeights = mapOf("cause_psu" to 5))
                    )
                ),
                Question(
                    id = "q_pwr_3",
                    text = "Have you made any of the following changes recently?",
                    options = listOf(
                        Option("opt_pwr_3a", "A", "Added new hardware (GPU, RAM, hard drive)", causeWeights = mapOf("cause_psu" to 4)),
                        Option("opt_pwr_3b", "B", "Updated software or OS driver", causeWeights = mapOf("cause_ram" to 1)),
                        Option("opt_pwr_3c", "C", "Moved or cleaned the PC / power strip", causeWeights = mapOf("cause_outlet" to 4, "cause_psu" to 3)),
                        Option("opt_pwr_3d", "D", "No changes made", causeWeights = mapOf("cause_psu" to 3, "cause_ram" to 2))
                    )
                )
            )
        ),

        // 2. Hardware: No display on monitor
        Issue(
            id = "issue_disp_01",
            categoryId = "cat_hardware",
            title = "No display on monitor",
            description = "PC turns on, fans spin, but the screen says 'No Signal' or stays black.",
            iconName = "tv_off",
            questions = listOf(
                Question(
                    id = "q_disp_1",
                    text = "What does the monitor display?",
                    options = listOf(
                        Option("opt_disp_1a", "A", "'No Signal' or 'Check Cable' message", causeWeights = mapOf("cause_cable" to 4, "cause_wrong_port" to 3)),
                        Option("opt_disp_1b", "B", "Blank black screen with power LED glowing/blinking", causeWeights = mapOf("cause_cable" to 3, "cause_wrong_port" to 3)),
                        Option("opt_disp_1c", "C", "Screen turns on then goes black after a second", causeWeights = mapOf("cause_cable" to 4)),
                        Option("opt_disp_1d", "D", "Distorted lines or weird artifacts before disappearing", causeWeights = mapOf("cause_cable" to 3))
                    )
                ),
                Question(
                    id = "q_disp_2",
                    text = "Where is the video cable (HDMI/DisplayPort) plugged into?",
                    options = listOf(
                        Option("opt_disp_2a", "A", "Plugged into the motherboard back I/O port instead of GPU", causeWeights = mapOf("cause_wrong_port" to 5)),
                        Option("opt_disp_2b", "B", "Plugged firmly into dedicated Graphics Card (GPU)", causeWeights = mapOf("cause_cable" to 4)),
                        Option("opt_disp_2c", "C", "Using an adapter or dongle (VGA to HDMI, etc.)", causeWeights = mapOf("cause_cable" to 5)),
                        Option("opt_disp_2d", "D", "I am not sure", causeWeights = mapOf("cause_wrong_port" to 3, "cause_cable" to 2))
                    )
                )
            )
        ),

        // 3. Hardware: Overheating / Sudden Shutdown
        Issue(
            id = "issue_heat_01",
            categoryId = "cat_hardware",
            title = "Computer overheating or shutting down",
            description = "Computer turns off suddenly while gaming, rendering, or after 15-30 minutes of usage.",
            iconName = "thermostat",
            questions = listOf(
                Question(
                    id = "q_heat_1",
                    text = "How do the PC fans sound right before it shuts down?",
                    options = listOf(
                        Option("opt_heat_1a", "A", "Fans spin extremely fast and loud like a jet engine", causeWeights = mapOf("cause_thermal_paste" to 4, "cause_dust" to 4)),
                        Option("opt_heat_1b", "B", "Fans are quiet or not spinning at all", causeWeights = mapOf("cause_dust" to 5)),
                        Option("opt_heat_1c", "C", "Normal noise level", causeWeights = mapOf("cause_thermal_paste" to 3)),
                        Option("opt_heat_1d", "D", "Laptop bottom/keyboard feels extremely hot to touch", causeWeights = mapOf("cause_dust" to 4, "cause_thermal_paste" to 4))
                    )
                ),
                Question(
                    id = "q_heat_2",
                    text = "When was the last time dust was cleaned or thermal paste replaced?",
                    options = listOf(
                        Option("opt_heat_2a", "A", "More than 2 years ago or never", causeWeights = mapOf("cause_dust" to 5, "cause_thermal_paste" to 4)),
                        Option("opt_heat_2b", "B", "Recently cleaned within the last 6 months", causeWeights = mapOf("cause_thermal_paste" to 4)),
                        Option("opt_heat_2c", "C", "It is a brand new PC/laptop", causeWeights = mapOf("cause_thermal_paste" to 3, "cause_dust" to 2)),
                        Option("opt_heat_2d", "D", "Laptop battery is swollen or bulging", isDangerous = true, dangerWarning = "SWOLLEN BATTERY HAZARD: Swollen lithium battery can catch fire! Stop using and do not charge.", causeWeights = mapOf("cause_thermal_paste" to 5))
                    )
                )
            )
        ),

        // 4. Internet: Wi-Fi connected but no internet
        Issue(
            id = "issue_wifi_01",
            categoryId = "cat_internet",
            title = "Wi-Fi connected but no internet",
            description = "Connected to Wi-Fi access point, but websites show 'DNS Probe Finished' or 'No Internet'.",
            iconName = "wifi_off",
            questions = listOf(
                Question(
                    id = "q_wifi_1",
                    text = "Do other devices (phones, tablets) have internet on the same Wi-Fi?",
                    options = listOf(
                        Option("opt_wifi_1a", "A", "Yes, other devices work fine", causeWeights = mapOf("cause_dns_cache" to 5)),
                        Option("opt_wifi_1b", "B", "No, all devices have no internet", causeWeights = mapOf("cause_router_isp" to 5)),
                        Option("opt_wifi_1c", "C", "Only websites load slowly or timeout", causeWeights = mapOf("cause_dns_cache" to 3)),
                        Option("opt_wifi_1d", "D", "I have not checked other devices", causeWeights = mapOf("cause_dns_cache" to 2, "cause_router_isp" to 2))
                    )
                ),
                Question(
                    id = "q_wifi_2",
                    text = "Have you restarted your Wi-Fi router or modem?",
                    options = listOf(
                        Option("opt_wifi_2a", "A", "No, I haven't restarted it yet", causeWeights = mapOf("cause_router_isp" to 4)),
                        Option("opt_wifi_2b", "B", "Yes, restarted router but problem persists", causeWeights = mapOf("cause_dns_cache" to 5)),
                        Option("opt_wifi_2c", "C", "Using a VPN or proxy server software", causeWeights = mapOf("cause_dns_cache" to 5)),
                        Option("opt_wifi_2d", "D", "Public Wi-Fi with login portal", causeWeights = mapOf("cause_router_isp" to 4))
                    )
                )
            )
        ),

        // 5. Software: Windows Blue Screen / OS Crash
        Issue(
            id = "issue_bsod_01",
            categoryId = "cat_software",
            title = "Windows Blue Screen (BSOD) or System Crash",
            description = "PC crashes suddenly with a blue screen error code like MEMORY_MANAGEMENT or CRITICAL_PROCESS_DIED.",
            iconName = "bug_report",
            questions = listOf(
                Question(
                    id = "q_bsod_1",
                    text = "When does the Blue Screen usually occur?",
                    options = listOf(
                        Option("opt_bsod_1a", "A", "Right during Windows boot up (Boot loop)", causeWeights = mapOf("cause_corrupt_sys" to 4, "cause_ram_fail" to 3)),
                        Option("opt_bsod_1b", "B", "When playing heavy games or launching specific software", causeWeights = mapOf("cause_ram_fail" to 4, "cause_corrupt_sys" to 2)),
                        Option("opt_bsod_1c", "C", "Randomly while idle or browsing", causeWeights = mapOf("cause_ram_fail" to 5, "cause_corrupt_sys" to 3)),
                        Option("opt_bsod_1d", "D", "Immediately after a driver or Windows update", causeWeights = mapOf("cause_corrupt_sys" to 5))
                    )
                ),
                Question(
                    id = "q_bsod_2",
                    text = "Did you see any specific error code on screen?",
                    options = listOf(
                        Option("opt_bsod_2a", "A", "MEMORY_MANAGEMENT / PAGE_FAULT_IN_NONPAGED_AREA", causeWeights = mapOf("cause_ram_fail" to 5)),
                        Option("opt_bsod_2b", "B", "INACCESSIBLE_BOOT_DEVICE / SYSTEM_THREAD_EXCEPTION", causeWeights = mapOf("cause_corrupt_sys" to 5)),
                        Option("opt_bsod_2c", "C", "VIDEO_TDR_FAILURE / nvlddmkm.sys", causeWeights = mapOf("cause_corrupt_sys" to 5)),
                        Option("opt_bsod_2d", "D", "I didn't catch the error code", causeWeights = mapOf("cause_ram_fail" to 3, "cause_corrupt_sys" to 3))
                    )
                )
            )
        ),

        // 6. Performance: High CPU or RAM usage
        Issue(
            id = "issue_perf_01",
            categoryId = "cat_performance",
            title = "100% CPU or RAM usage / Severe Lag",
            description = "Task manager shows CPU or RAM stuck at 90-100%, causing mouse stutters and extreme slowness.",
            iconName = "equalizer",
            questions = listOf(
                Question(
                    id = "q_perf_1",
                    text = "Which process is using the most resources in Task Manager?",
                    options = listOf(
                        Option("opt_perf_1a", "A", "System / Windows Search / Antivirus Background Scan", causeWeights = mapOf("cause_win_indexing" to 4)),
                        Option("opt_perf_1b", "B", "Web browser (Chrome/Edge with dozens of tabs)", causeWeights = mapOf("cause_browser_tabs" to 5)),
                        Option("opt_perf_1c", "C", "An unknown executable file with high CPU usage", causeWeights = mapOf("cause_malware" to 5)),
                        Option("opt_perf_1d", "D", "I am unable to open Task Manager", causeWeights = mapOf("cause_malware" to 3, "cause_win_indexing" to 3))
                    )
                )
            )
        ),

        // 7. Other: Liquid Spill
        Issue(
            id = "issue_spill_01",
            categoryId = "cat_other",
            title = "Water or liquid spill on laptop/desktop",
            description = "Coffee, water, or soda spilled onto the laptop keyboard or desktop tower.",
            iconName = "water_drop",
            questions = listOf(
                Question(
                    id = "q_spill_1",
                    text = "Is the device currently powered on or connected to charger?",
                    options = listOf(
                        Option("opt_spill_1a", "A", "Yes, plugged in and turned on", isDangerous = true, dangerWarning = "ELECTROCUTION & SHORT CIRCUIT DANGER: Immediately disconnect power charger and hold power button 10s to force shutdown!", causeWeights = mapOf("cause_liquid_spill" to 5)),
                        Option("opt_spill_1b", "B", "Shut down immediately and unplugged", causeWeights = mapOf("cause_liquid_spill" to 5)),
                        Option("opt_spill_1c", "C", "It turned off by itself with smoke/pop sound", isDangerous = true, dangerWarning = "CRITICAL HARDWARE DAMAGE: Internal component blown. Do not attempt to turn on!", causeWeights = mapOf("cause_liquid_spill" to 5))
                    )
                )
            )
        )
    ) + expandedIssues()).associateBy { it.id }

    // Causes & Step-by-Step Solutions
    private val causesMap: Map<String, Cause> = (listOf(
        Cause(
            id = "cause_psu",
            issueId = "issue_pwr_01",
            title = "Power Supply Unit (PSU) or Loose Cable",
            description = "The Power Supply Unit (PSU) may be faulty, turned off at the back switch, or not supplying enough power to the motherboard.",
            solutions = listOf(
                SolutionStep(1, "Check Power Cable Connection", "Ensure the power cable is firmly pushed into the back of the PC power supply unit and securely plugged into the wall outlet.", "img_psu_repair_1785497672881", "Make sure the wall outlet has power by testing with a lamp or charger."),
                SolutionStep(2, "Toggle PSU Switch", "Locate the rocker switch next to the power plug on the back of the desktop case. Set it to 'I' (ON) instead of 'O' (OFF).", null, null),
                SolutionStep(3, "Bypass Surge Protector / UPS", "Plug the PC directly into the wall outlet to rule out a tripped surge protector or faulty battery backup unit.", null, null),
                SolutionStep(4, "Perform Paperclip Test or Replace PSU", "If the PC remains completely silent with no fan movement, the internal PSU fuse or capacitor has likely blown and needs replacement.", null, "Do NOT open the power supply unit casing yourself as high-voltage capacitors hold lethal charge.")
            )
        ),
        Cause(
            id = "cause_outlet",
            issueId = "issue_pwr_01",
            title = "Faulty Wall Outlet or Surge Protector",
            description = "The electrical outlet or power strip is tripped or not delivering voltage.",
            solutions = listOf(
                SolutionStep(1, "Test Wall Outlet", "Plug a lamp or phone charger into the wall socket to confirm electricity is flowing.", null, null),
                SolutionStep(2, "Reset Surge Protector Switch", "Check if the red breaker switch on your power strip has tripped to the Off position.", null, null)
            )
        ),
        Cause(
            id = "cause_ram",
            issueId = "issue_pwr_01",
            title = "Unseated or Oxidized RAM Sticks",
            description = "The system memory (RAM) module has loose contact pins, preventing the motherboard POST (Power-On Self-Test) sequence from completing.",
            solutions = listOf(
                SolutionStep(1, "Unplug PC and Touch Grounded Metal", "Disconnect the power cable and touch an unpainted metal part of the PC case to discharge static electricity.", null, null),
                SolutionStep(2, "Open Side Panel & Open DIMM Latches", "Press down on the plastic latches at both ends of the RAM slot on the motherboard to release the memory stick.", "img_ram_repair_1785497686389", "Handle RAM modules carefully by holding the side edges only."),
                SolutionStep(3, "Re-insert RAM Firmly Until Click", "Align the notch on the bottom of the RAM stick with the key in the slot, then press down firmly with both thumbs until latches snap shut automatically.", "img_ram_repair_1785497686389", "You should hear a firm 'click' sound when properly seated.")
            )
        ),
        Cause(
            id = "cause_wrong_port",
            issueId = "issue_disp_01",
            title = "Display Cable Plugged into Integrated Graphics Port",
            description = "Your computer has a dedicated Graphics Card (GPU), but your HDMI/DisplayPort cable is plugged into the top motherboard port instead.",
            solutions = listOf(
                SolutionStep(1, "Locate Dedicated GPU Ports", "Look at the back lower half of your PC case for horizontal display ports attached directly to the graphics card.", null, null),
                SolutionStep(2, "Move Display Cable", "Unplug the cable from the motherboard I/O panel (upper vertical ports) and plug it into the GPU port.", null, "Restart the PC after switching ports.")
            )
        ),
        Cause(
            id = "cause_cable",
            issueId = "issue_disp_01",
            title = "Faulty HDMI/DisplayPort Cable or Monitor Input Source",
            description = "The video cable is damaged or the monitor is set to the wrong input source (e.g. HDMI 1 vs DisplayPort).",
            solutions = listOf(
                SolutionStep(1, "Reseat Both Ends of Cable", "Unplug the video cable from both the PC and monitor, check for bent pins, and reconnect tightly.", null, null),
                SolutionStep(2, "Check Monitor Input Settings", "Use the physical buttons on your monitor to select the correct Input Source (HDMI 1, HDMI 2, or DP).", null, null)
            )
        ),
        Cause(
            id = "cause_dust",
            issueId = "issue_heat_01",
            title = "Dust Clogging Heatsink Fans",
            description = "Dust accumulation inside heat sink fins blocks airflow, causing CPU temperature to exceed safety limits (100°C) and shut down.",
            solutions = listOf(
                SolutionStep(1, "Power Off and Open Case", "Unplug PC power cord and open the case side panel.", null, null),
                SolutionStep(2, "Use Compressed Air", "Hold fan blades still with a finger and blow compressed air through heatsink fins and fan blades in short bursts.", null, "Never let fans spin at high speeds from compressed air as it can damage bearings or generate voltage.")
            )
        ),
        Cause(
            id = "cause_thermal_paste",
            issueId = "issue_heat_01",
            title = "Dried Out Thermal Compound",
            description = "The thermal interface material between the CPU heat spreader and cooler heatsink has dried up, losing heat conductivity.",
            solutions = listOf(
                SolutionStep(1, "Remove CPU Cooler", "Unscrew CPU cooler bracket, clean old dry thermal paste using 90%+ Isopropyl Alcohol.", null, null),
                SolutionStep(2, "Apply Pea-Sized Thermal Paste", "Place a small pea-sized drop of fresh high-performance thermal paste in the center of the CPU.", null, null)
            )
        ),
        Cause(
            id = "cause_dns_cache",
            issueId = "issue_wifi_01",
            title = "Corrupt Local DNS Resolver Cache or Network Config",
            description = "The operating system DNS cache is storing outdated domain addresses, preventing websites from resolving IP numbers.",
            solutions = listOf(
                SolutionStep(1, "Open Command Prompt as Admin", "Search for 'cmd' in Windows, right-click and select 'Run as Administrator'.", null, null),
                SolutionStep(2, "Run Flush DNS Command", "Type `ipconfig /flushdns` and press Enter. You should see 'Successfully flushed the DNS Resolver Cache'.", null, null),
                SolutionStep(3, "Reset Network Stack", "Type `netsh winsock reset` and restart your computer.", null, null)
            )
        ),
        Cause(
            id = "cause_router_isp",
            issueId = "issue_wifi_01",
            title = "Router Gateway Freeze or ISP Outage",
            description = "The Wi-Fi router DNS/DHCP gateway service stopped responding or internet provider line is down.",
            solutions = listOf(
                SolutionStep(1, "Restart Router / Modem", "Unplug the router power cable for 30 seconds, then plug back in and wait 2 minutes for lights to stabilize.", null, null)
            )
        ),
        Cause(
            id = "cause_ram_fail",
            issueId = "issue_bsod_01",
            title = "Corrupted System RAM or Memory Timings",
            description = "Faulty memory sectors cause Windows kernel memory access violations.",
            solutions = listOf(
                SolutionStep(1, "Run Windows Memory Diagnostic", "Press Windows Key + R, type `mdsched.exe`, select 'Restart now and check for problems'.", null, null),
                SolutionStep(2, "Test Single Stick of RAM", "If you have 2 RAM sticks, remove one and boot PC to isolate which module is causing crashes.", null, null)
            )
        ),
        Cause(
            id = "cause_corrupt_sys",
            issueId = "issue_bsod_01",
            title = "Corrupted System Files or Disk Sector Error",
            description = "Critical Windows system files (`ntoskrnl.exe` or system drivers) are corrupted or damaged.",
            solutions = listOf(
                SolutionStep(1, "Run System File Checker (SFC)", "Open Command Prompt as Admin and run `sfc /scannow`.", null, null),
                SolutionStep(2, "Run DISM Repair Tool", "Run `dism /online /cleanup-image /restorehealth` to repair Windows component store.", null, null)
            )
        ),
        Cause(
            id = "cause_liquid_spill",
            issueId = "issue_spill_01",
            title = "Liquid Contamination & Short Circuit Hazard",
            description = "Liquid trapped inside electronic circuits conducts electricity where it shouldn't, risking motherboard burnout.",
            solutions = listOf(
                SolutionStep(1, "Power Off Immediately", "Press and hold power button for 10 seconds. Unplug AC adapter immediately.", null, "Do NOT turn on the laptop to test if it works!"),
                SolutionStep(2, "Invert Laptop in V-Shape", "Place laptop upside down on a dry towel in an inverted 'V' tent position so liquid drains away from motherboard.", null, null),
                SolutionStep(3, "Air Dry for Minimum 48 Hours", "Leave in a warm, dry room with air circulation for 48-72 hours before attempting power on.", null, "Do NOT use a hairdryer on high heat as it melts keyboard keycaps.")
            )
        )
    ) + expandedCauses()).associateBy { it.id }

    fun getAllCategories(): List<Category> = categories

    fun getCategoryById(id: String): Category? = categories.find { it.id == id }

    fun getIssuesByCategoryId(catId: String): List<Issue> {
        return issuesMap.values.filter { it.categoryId == catId }
    }

    fun getIssueById(issueId: String): Issue? = issuesMap[issueId]

    fun getCauseById(causeId: String): Cause? = causesMap[causeId]

    fun getAllIssues(): List<Issue> = issuesMap.values.toList()

    fun searchIssues(query: String): List<Issue> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        return issuesMap.values.filter {
            it.title.lowercase().contains(q) ||
            it.description.lowercase().contains(q) ||
            it.searchTerms.any { term -> term.lowercase().contains(q) } ||
            it.questions.any { question ->
                question.text.lowercase().contains(q) ||
                    question.options.any { option -> option.text.lowercase().contains(q) }
            } ||
            causesMap.values.any { cause ->
                cause.issueId == it.id &&
                    (cause.title.lowercase().contains(q) || cause.description.lowercase().contains(q))
            }
        }
    }

    fun evaluateDiagnosis(issueId: String, selectedOptionIds: Map<String, String>): DiagnosticResult? {
        val issue = issuesMap[issueId] ?: return null
        if (issue.questions.isEmpty() || issue.questions.any { selectedOptionIds[it.id] == null }) return null
        val causeScores = mutableMapOf<String, Int>()
        var dangerWarningMessage: String? = null
        val userAnswersSummary = mutableMapOf<String, String>()

        // Process answers and score candidate causes
        issue.questions.forEach { question ->
            val selectedOptionId = selectedOptionIds[question.id]
            val selectedOption = question.options.find { it.id == selectedOptionId }
            if (selectedOption != null) {
                userAnswersSummary[question.text] = selectedOption.text
                if (selectedOption.isDangerous && selectedOption.dangerWarning != null) {
                    dangerWarningMessage = selectedOption.dangerWarning
                }
                selectedOption.causeWeights.forEach { (causeId, weight) ->
                    causeScores[causeId] = (causeScores[causeId] ?: 0) + weight
                }
            }
        }

        // Get candidate causes for this issue
        val relevantCauses = causesMap.values.filter { it.issueId == issueId }
        if (relevantCauses.isEmpty()) return null

        // Rank causes by score
        val sortedCauses = relevantCauses.sortedWith(
            compareByDescending<Cause> { causeScores[it.id] ?: 0 }.thenBy { it.id }
        )
        val primaryCause = sortedCauses.first()
        val altCauses = sortedCauses.drop(1).take(2)

        val maxPossibleScore = issue.questions.size * 5
        val topScore = causeScores[primaryCause.id] ?: 0
        val secondScore = sortedCauses.getOrNull(1)?.let { causeScores[it.id] ?: 0 } ?: 0
        val evidenceRatio = topScore.toDouble() / maxPossibleScore.toDouble()
        val marginRatio = (topScore - secondScore).coerceAtLeast(0).toDouble() / maxPossibleScore.toDouble()
        val confidence = (45 + evidenceRatio * 35 + marginRatio * 20).toInt().coerceIn(45, 96)

        return DiagnosticResult(
            issue = issue,
            mostLikelyCause = primaryCause,
            confidencePercentage = confidence,
            alternativeCauses = altCauses,
            userAnswers = userAnswersSummary,
            triggeredDangerWarning = dangerWarningMessage
        )
    }

    /** Returns human-readable catalog defects. An empty list means every diagnostic path is usable. */
    fun validateCatalog(): List<String> {
        val errors = mutableListOf<String>()
        val issueIds = issuesMap.keys
        val causeIds = causesMap.keys

        categories.forEach { category ->
            val actualCount = issuesMap.values.count { it.categoryId == category.id }
            if (actualCount != category.issueCount) {
                errors += "${category.id} advertises ${category.issueCount} issues but contains $actualCount"
            }
        }
        issuesMap.values.forEach { issue ->
            if (issue.categoryId !in categories.map { it.id }.toSet()) errors += "${issue.id} has an unknown category"
            if (issue.questions.isEmpty()) errors += "${issue.id} has no diagnostic questions"
            if (causesMap.values.none { it.issueId == issue.id }) errors += "${issue.id} has no repair causes"
            issue.questions.forEach { question ->
                if (question.options.isEmpty()) errors += "${question.id} has no answer options"
                if (question.options.map { it.letter }.distinct().size != question.options.size) {
                    errors += "${question.id} has duplicate option letters"
                }
                question.options.forEach { option ->
                    option.causeWeights.keys.forEach { causeId ->
                        if (causeId !in causeIds) errors += "${option.id} references missing cause $causeId"
                        else if (causesMap[causeId]?.issueId != issue.id) errors += "${option.id} references a cause for another issue"
                    }
                }
            }
        }
        causesMap.values.forEach { cause ->
            if (cause.issueId !in issueIds) errors += "${cause.id} references missing issue ${cause.issueId}"
            if (cause.solutions.isEmpty()) errors += "${cause.id} has no repair steps"
            if (cause.solutions.map { it.stepNumber } != (1..cause.solutions.size).toList()) {
                errors += "${cause.id} has non-sequential repair steps"
            }
            if (cause.estimatedMinutes <= 0) errors += "${cause.id} has an invalid repair time"
        }
        return errors
    }
}
