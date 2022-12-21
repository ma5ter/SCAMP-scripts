{"script_uid":"beacon-classic-any-w4hda-earghb6","category":[{"iso":"ru","value":"/Маяки"},{"iso":"en","value":"/Beacons"},{"iso":"es","value":"/Balizas"},{"iso":"de","value":"/Leuchten"}],"name":[{"iso":"ru","value":"Маяк (все типы разделов)"},{"iso":"en","value":"Beacon"},{"iso":"es","value":"Baliza"},{"iso":"de","value":"Leuchte"}],"description":[{"iso":"ru","value":"Сценарий 'Маяк' меняет состояние управляемого им выхода в зависимости от состояния (взят/снят) и наличия тревоги в отслеживаемых разделах следующим образом:\n - выход включается при постановке на охрану всех отслеживаемых разделов(непрерывное свечение маяка);\n - выход выключается при снятии с охраны любого из отслеживаемых разделов(маяк не светится);\n - выход включается и выключается с заданной периодичностью при тревоге в любом из отслеживаемых разделов(прерывистое свечение маяка)"},{"iso":"en","value":"The 'Beacon' scenario changes the state of the output depending on the state (armed/disarmed) or presence of alarm events of the assigned partitions as follows:\n - the output turns on when arming all assigned partitions (continuous light);\n - the output turns off when disarming any of the assigned partitions (not lit);\n - the output turns on and off with the specified frequency when an alarm event occurs in any of assigned partitions (intermittent light)"},{"iso":"es","value":"El escenario 'Baliza' cambia el estado de la salida dependiendo del estado (armado/desarmado) o la presencia de evento de alarma en las secciones asignadas de la siguiente manera:\n - la salida se enciende cuando se establece en armado de todas las secciones asignadas (luz continua);\n - la salida se apaga al desarmar cualquiera de las secciones asignadas (la baliza no se enciende);\n - la salida se enciende y se apaga con la frecuencia especificada cuando una alarma ocurre en cualquiera de las secciones asignadas (luz intermitente)"},{"iso":"de","value":"Das Szenario \"Leuchte\" ändert den Zustand des gesteuerten Relais in Abhängigkeit von Zuständen (scharf/unscharf) und Alarm in den überwachten Bereichen wie folgt: - der Ausgang schaltet sich aus, wenn alle Bereiche scharfgeschaltet werden (signalisiert durch das ununterbrochene Leuchten); - der Ausgang schaltet sich aus, sobald einer der überwachten Bereiche unscharfgeschaltet wird (die Leuchte bleibt aus); - der Ausgang schaltet sich mit festgelegter Periodizität ein und aus bei Alarm in einem der überwachten Bereiche (signalisiert durch das unterbrochene Leuchten)."}],"description_alt":[{"iso":"ru","value":"Сценарий <b>Маяк</b> меняет состояние управляемого им реле в зависимости от состояния <b>взятия/снятия и наличия тревоги</b> в отслеживаемых разделах следующим образом:\n\n-реле <b>включается</b> при постановке на охрану всех отслеживаемых разделов(непрерывное свечение маяка);\n-<b>выключается</b> при снятии с охраны любого из отслеживаемых разделов(маяк не светится);\n-включается и выключается с заданной периодичностью при тревоге в любом из отслеживаемых разделов(<b>прерывистое свечение маяка</b>)"},{"iso":"en","value":"The <b>Beacon</b> scenario changes the state of the output depending on the state <b>armed/disarmed or alarm events </b> of the assigned partitions as follows: -the output is <b>ON</b> when arming all assigned partitions (continuous light); -the output turns <b>OFF</b> when disarming any of the assigned partitions (not lit); - turns ON and OFF with the specified frequency when an alarm event occurs in any of the assigned partitions (<b >intermittent light</b>)"},{"iso":"es","value":"El escenario <b>Baliza</b> cambia el estado de la salida dependiendo del estado <b>armado/desarmado o evento de alarma</b> en las secciones asignadas de la siguiente manera: -la salida <b>se enciende</b> cuando se establece en armado de todas las secciones asignadas (luz continua); -la salida <b> se apaga </b> al desarmar cualquiera de las secciones asignadas (la baliza no se enciende); - se enciende y se apaga con la frecuencia especificada cuando ocurre una alarma en cualquiera de las secciones asignadas (<b >luz intermitente de la baliza</b>)"},{"iso":"de","value":"Das Szenario <b>Leuchte</b> ändert den Zustand des gesteuerten Relais in Abhängigkeit von Zuständen <b>Scharf-/Unscharfschaltung und Alarm</b> in den überwachten Bereichen wie folgt:<br> - das Relais <b>wird eingeschaltet,</b> sobald alle überwachten Bereiche scharfgeschaltet werden (die Leuchte bleibt ununterbrochen an);<br> - das Relais <b>wird ausgeschaltet,</b> sobald einer der überwachten Bereiche unscharfgeschaltet wird (die Leuchte ist aus);<br>- das Relais wird mit festgelegter Periodizität ein- und ausgeschaltet in Alarmfall in einem der überwachten Bereiche (<b>das Licht bleibt dauerhaft an</b>)."}],"source":"; custom blink scheme by ma5ter\n\ndecimate_timer equ 6\nblink_mask equ 0\n;sections equ 3\n\n; common TOS sizes\nb equ 0\nw equ 1\ndw equ 2\n\n; results\noff equ 0\non equ 1\n\nsection_count equ 8\n\n; remove after device.xml full support\nTICKS equ 0\nALARM equ 23\nARMED_SECTIONS equ 30\nARMABLE_SECTIONS equ 33\nSECTION_TYPE_BASE equ 128\nGUARD_SECTIONS equ 33\n\npb macro ; byte\n psh (($1 >> 4) & 15)\n nib ($1 & 15)\nendm\n\npw macro ; word\n psh (($1 >> 12) & 15)\n nib (($1 >> 8) & 15)\n psh (($1 >> 4) & 15)\n nib ($1 & 15)\nendm\n\npd macro ; dword\n psh (($1 >> 28) & 15)\n nib (($1 >> 24) & 15)\n psh (($1 >> 20) & 15)\n nib (($1 >> 16) & 15)\n psh (($1 >> 12) & 15)\n nib (($1 >> 8) & 15)\n psh (($1 >> 4) & 15)\n nib ($1 & 15)\nendm\n\n; mov instruction with parameters\nup macro\n psh (((($1 & 3) | ($2 << 2)) >> 4) & 15)\n nib (($1 & 3) | ($2 << 2) & 15)\n mov\nendm\n\n; exp instruction with parameters\nex macro\n psh ($1 & 3)\n nib ((($3 & 1) << 3) | ($2 & 3))\n exp\nendm\n\nshift_l macro\n dec b\n psh $1\n shl 1\n swp b\n dec b\n snz\n bra 2\n swp b\n bra -7\n pop b\nendm\n\n; BEGIN\n ; TOS on entry:\n ; b: previous result\n ;   b: ticks\n ; store zone counter\n pop b ; dispose previous result\n\nticks\n ; get ticks\n pb TICKS\n inp b, w\n \n ; decimate timer\n pw 1 << decimate_timer\n div w\n ; collapse to unsigned byte\n ex w, b, 0\n \n dup b\n up b, 2\n cmp b\n \n ; wait for not equal \n snz\n bra ticks\n \n ; get alarm flag\n pb ALARMING_SECTIONS\n inp b, b\n pb sections\n and b\n pop b\n snz\n bra check_zone\n\nalarm\n ; blink according to the lsb of ticks\n dup b\n psh 1\n and b\n ret\n\ncheck_zone\n pw ZONE_SECTION_BASE\nloop\n dup w\n inp w, b\n dup b\n pb 254\n and b\n pop b\n snz\n bra label\n shift_l 1\nlabel\n pb sections\n and b\n pop b\n skz\n bra check_delay ; zone to check\n inc w\n dup w\n pw 4128\n cmp w\n skz\n bra loop\n bra no_alarm_dispose\n \ncheck_delay\n dup w\n pw ZONE_SECTION_BASE\n sub w\n pw ZONE_EXITDELAY_FLAG_BASE\n add w\n inp w, b\n skz\n bra dispose\n pop b\n inc w\n dup w\n pw 4128\n cmp w\n snz\n bra no_alarm_dispose\n psh loop\n jmp b\n \ndispose\n pop dw\n psh 0\n ret\n \nno_alarm_dispose\n pop w\nno_alarm\n ; get armed mask\n pb ARMED_SECTIONS\n inp b, b\n pb GUARD_SECTIONS\n inp b, b\n and b\n pb sections\n and b\n skz\n bra armed\n ; no need to pop/push, TOS is already zero\n pop b\n psh 0\n ret\n\narmed\n pb sections\n cmp b\n skz\n bra partial\n\nfull\n psh on\n ret\n\npartial\n ; blink according to zero value\n ; in 3 lsb of ticks\n dup b\n psh (1 << blink_mask) - 1\n and b\n snz\n ret\n pop b\n bra full","params":[{"name":"sections","format":"^([0-9]|[1-8][0-9]|9[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$","type":"sections","description":[{"iso":"ru","value":"Отслеживаемые разделы"},{"iso":"en","value":"Partitions"},{"iso":"es","value":"Secciones"},{"iso":"de","value":"Überwachte Bereiche"}]}],"firmwares":["67111504","21954820","21889283","21954819","22020353","22020355","22020356","22151427","22216961","22216963","22216964","22282499","22348035","22413571","22479107","22544641","22544643","22610177","22610179","22675715","22741251","39452932","22806787","22872323","39584004","22937859","39649540","23068931","39846148","23134467","39911684","39977220","23265539","40042756","40173828","23527683","23593219","23265537","40435972","23658755","23724291","23789827","23855363","40632580","23920899"]}