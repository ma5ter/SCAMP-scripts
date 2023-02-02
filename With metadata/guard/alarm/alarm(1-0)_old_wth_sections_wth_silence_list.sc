{"script_uid":"guard-alarmi-asdfg-fhjdf9-g4t-01","category":[{"iso":"ru","value":"/Охрана"},{"iso":"en","value":"/Protection"},{"iso":"es","value":"/Protección"},{"iso":"de","value":"/Sicherheit"}],"name":[{"iso":"ru","value":"Тревога"},{"iso":"en","value":"Alarm"},{"iso":"es","value":"Alarma"},{"iso":"de","value":"Alarm"}],"description":[{"iso":"ru","value":"Сценарий 'Тревога' меняет состояние управляемого им реле в зависимости от состояния отслеживаемых разделов следующим образом:/n - выход отключается только при появлении тревоги, нарушения или пожара в любом из отслеживаемых разделов;\n - во всех остальных случаях поддерживается включенное состояние реле.\nДля применения обратной логики работы сценария необходимо включить инверсию при настройке параметров сценария."},{"iso":"en","value":"The 'Alarm' scenario changes the state of the output depending on the state of the assigned partitions as follows:\n - the output is turned off only when an alarm, violation or fire alarm event occurs in any of assigned partitions;\n - in all other cases the output is turned on.\nInversion mode of the scenario is set by setting Inversion parameter to ON."},{"iso":"es","value":"El escenario 'Alarma' cambia el estado de la salida dependiendo del estado de las secciones monitoreadas de la siguiente manera:/n - la salida está apagada sólo cuando un evento de alarma, violación o incendio ocurre en cualquiera de secciones monitoreadas;\n - en todos los demás casos, se admite el estado apagado de la salida.\nPara utilizar la lógica inversa del escenario, es necesario activar el parámetro Inversión."},{"iso":"de","value":"Das Szenario \"Alarm\" ändert den Zustand des gesteuerten Relais in Abhängigkeit von Zuständen (scharf/unscharf) der überwachten Bereiche wie folgt: - der Ausgang wird nur im Alarm-, Stör- oder Brandfall in einem der zugewiesenen Bereiche (wenn die Verzögerungszeit eingestellt ist, dann nach Ablauf dieser Zeit) eingeschaltet; - der Ausgang wird ausgeschaltet, sobald einer der überwachten Bereiche unscharfgeschaltet wird. Der umgekehrte Betriebsmodus kann durch die Wahl der Option \"Invertieren\" in den Einstellungen "}],"description_alt":[{"iso":"ru","value":"Сценарий <b>Тревога</b> меняет состояние управляемого им реле в зависимости от состояния отслеживаемых разделов следующим образом:<br><br>-реле <b>отключается</b> только при появлении тревоги, нарушения или пожара в любом из отслеживаемых разделов;<br>-во всех остальных случаях поддерживается <b>включенное</b> состояние реле.<br><br>Для применения обратной логики работы сценария необходимо включить инверсию при настройке параметров сценария."},{"iso":"en","value":"The <b> Alarm </b> scenario changes the state of the output depending on the state of the assigned partitions as follows: <br> <br> -the output is <b>OFF</b> only when an alarm, violation or fire event occurs in any of assigned partitions;<br>-in all other cases the output is <b>ON</b>. <br> <br>Inversion mode of the scenario is set by setting Inversion parameter to ON."},{"iso":"es","value":"El escenario <b>Alarma</b> cambia el estado de la salida dependiendo del estado de las secciones monitoreadas de la siguiente manera: <br> <br>- la salida está<b> apagada</b> sólo cuando ocurre un evento de alarma, violación o incendio en cualquiera de secciones monitoreadas; <br>- en todos los demás casos, se admite el estado <b>apagado</b> de la salida . <br> <br> Para utilizar la lógica inversa del escenario, es necesario activar el parámetro Inversión."},{"iso":"de","value":"Das Szenario <b>Alarm</b> ändert den Zustand des gesteuerten Relais in Abhängigkeit von Zuständen der überwachten Bereiche wie folgt:<br><br>- das Relais <b>schaltet sich aus</b> im Alarm-, Stör- oder Brandfall in einem der Bereiche;<br>- in allen anderen Fällen bleibt das Relais <b>eingeschaltet</b>.<br><br>Für die Umkehrung der Arbeitslogik des Szenarios muss die Invertierung in den Einstellungen des Szenarios programmiert werden."}],"source":"; common TOS sizes\nb equ 0\nw equ 1\ndw equ 2\n\n;action equ 1 ; (param) active state\n;sections equ 13 ; (param) monitored sections\n\npb macro ; byte\n psh (($1 >> 4) & 15)\n nib ($1 & 15)\nendm\n\n; push word macro\npw macro\n psh (($1 >> 12) & 15)\n nib (($1 >> 8) & 15)\n psh (($1 >> 4) & 15)\n nib ($1 & 15)\nendm\n\n; find mask of section\n; (shift 1 left while section number > 0)\nfind_section_mask macro\n dec b  ; 'section number' - 1\n psh $1 ; initiate mask\n shl 1  ; shift left mask\n swp b  ; bring 'section number' up\n dec b  ; 'section number' - 1\n ; if 'section number' is 0 - dispose 'section number' and leave macro\n ; else - bring mask up and continue shifting\n snz\n bra 2\n swp b  ; bring mask up\n bra -7\n pop b ; dispose 'section number'\nendm\n\n; BEGIN\n pop b ; dispose previous result\n\n; check zones for alarms\ncheck_troubles\n pw ZONE_SECTION_BASE ; put first 'zone section info' address\nloop ; \n dup w ; duplicate current 'zone setion info' address\n inp w, b ; get section number for current zone\n dup b\n ; check if section number is anything but 1 or 0\n ; if yes - find section mask\n ; else - continue\n pb 254 ; anything but 1 or 0\n and b\n pop b\n snz\n bra do_not_shift\n find_section_mask 1\ndo_not_shift\n pb sections\n pb ARMED_SECTIONS\n inp b, b\n pb GUARD_SECTIONS\n inp b, b\n inv b ; get mask of all type of sections but guard\n ior b ; calculate mask of 'all type of sections' and 'armed guard'\n and b ; calculate which ones is monitored\n ; check if zone section belongs to monitored\n ; if yes - check alarm on zone\n ; else - continue\n and b\n pop b\n skz\n bra check_alarm ; zone to check\nnext_zone\n inc w ; increment zone section info address\n ; compare current zone section info address and last\n ; if equal - go to no_alarm_dispose\n ; else - continue loop\n dup w\n pw 4128 ; zone section info address of last zone\n cmp w\n snz\n bra no_alarm_dispose\n pb loop\n jmp b\n \ncheck_alarm\n ; get zone number from current 'zone section info' address\n dup w\n pw ZONE_SECTION_BASE\n sub w\n ; add this 'zone number' to 'zone alarm flag' address\n pw ZONE_ALARM_FLAG_BASE\n add w\n inp w, b ; get alarm flag of this zone\n ; if zone has alarm - dispose and set_off\n ; else - check next zone\n skz\n bra has_alarm\n pop b\n bra next_zone\n\nhas_alarm\n pop dw ; dispose 'alarm flag' and current 'zone section info' address\n pb set_off\n jmp b\n \nno_alarm_dispose\n pop w ; dispose current 'zone section info' address\nno_alarm\n ; (I really don’t remember why this piece of code is needed here)\n ; check panic flag\n ; if has - set off\n ; else - set on\n psh PANIC\n inp b, b\n pop b\n skz\n bra set_off\n \nset_on\n psh action & 1\n ret\n \nset_off\n psh ~action & 1\n ret","params":[{"name":"action","format":"^([01])$","type":"list","default":"1","extra":[{"name":"turn_on","description":[{"iso":"ru","value":"Нет"},{"iso":"en","value":"OFF"},{"iso":"es","value":"Desactivado"},{"iso":"de","value":"Nein"}],"value":"1"},{"name":"turn_off","description":[{"iso":"ru","value":"Да"},{"iso":"en","value":"ON"},{"iso":"es","value":"Activado"},{"iso":"de","value":"Ja"}],"value":"0"}],"description":[{"iso":"ru","value":"Инверсия"},{"iso":"en","value":"Inversion"},{"iso":"es","value":"Inversión"},{"iso":"de","value":"Invertierung"}]},{"name":"sections","format":"^([0-9]|[1-8][0-9]|9[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$","type":"sections","description":[{"iso":"ru","value":"Отслеживаемые разделы"},{"iso":"en","value":"Partitions"},{"iso":"es","value":"Secciones "},{"iso":"de","value":"Überwachte Bereiche"}]}],"firmwares":["67111504","21954820","21889283","21954819","22020353","22020355","22020356","22151427","22216961","22216963","22216964","22282499","22348035","22413571","22479107","22544641","22544643","22610177","22610179","22675715","22741251","39452932","22806787","22872323","39584004","22937859","39649540","23068931","39846148","23134467","39911684","39977220","23265539","40042756","40173828","23527683","23593219","23265537","40435972","23658755","23724291","23789827","23855363","40632580","23920899","24117507","24183043"]}