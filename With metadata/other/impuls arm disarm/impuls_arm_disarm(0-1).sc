{"script_uid":"smart-impuls-arm-disarm-oe4-nf33","category":[{"iso":"ru","value":"/Умный дом"},{"iso":"en","value":"/Smart Home"},{"iso":"es","value":"/Casa inteligente"},{"iso":"de","value":"/Smart Home"}],"name":[{"iso":"ru","value":"Импульсный по взятию/снятию"},{"iso":"en","value":"Impulse on arm/disarm"},{"iso":"es","value":"Impulso al armar/desarmar"},{"iso":"de","value":"Impuls zum Scharf-/Unscharfschalten"}],"description":[{"iso":"ru","value":"Сценарий 'Импульсный по взятию/снятию' позволяет переключать состояние выхода на заданный промежуток времени по взятию/снятию разделов с охраны."},{"iso":"en","value":"Scenario 'Impulse on arm/disarm' allows you to switch the output state for a specified period of time on arming and disarming partitions."},{"iso":"es","value":"El escenario 'Impulso al armar/desarmar' le permite cambiar el estado de salida durante un período de tiempo específico al armar y desarmar particiones."},{"iso":"de","value":"Das Szenario „Impuls beim Scharf-/Unscharfschalten“ ermöglicht es Ihnen, den Ausgangszustand für eine bestimmte Zeitdauer beim Scharf- und Unscharfschalten von Bereichen umzuschalten."}],"description_alt":[{"iso":"ru","value":"Сценарий <b>Импульсный по взятию/снятию</b> позволяет переключать состояние выхода на заданный промежуток времени по взятию/снятию разделов с охраны."},{"iso":"en","value":"Scenario <b>Impulse on arm/disarm</b> allows you to switch the output state for a specified period of time on arming and disarming partitions."},{"iso":"es","value":"El escenario <b>Impulso al armar/desarmar</b> le permite cambiar el estado de salida durante un período de tiempo específico al armar y desarmar particiones."},{"iso":"de","value":"Das Szenario <b>Impuls beim Scharf-/Unscharfschalten</b> ermöglicht es Ihnen, den Ausgangszustand für eine bestimmte Zeitdauer beim Scharf- und Unscharfschalten von Bereichen umzuschalten."}],"case":[{"iso":"ru","value":"1. Открытие замка на несколько секунд"}],"source":"; common TOS sizes\nb equ 0\nw equ 1\ndw equ 2\n\ntimer equ 5 ; (param) impuls duration\naction equ 1 ; (param) active state\nsections equ 3 ; (param) mask of monitored sections\n\npb macro ; byte\n psh (($1 >> 4) & 15)\n nib ($1 & 15)\nendm\n\n; push word macro\npw macro\n psh (($1 >> 12) & 15)\n nib (($1 >> 8) & 15)\n psh (($1 >> 4) & 15)\n nib ($1 & 15)\nendm\n\n; exp instruction with parameters\nex macro\n psh ($1 & 3)\n nib ((($3 & 1) << 3) | ($2 & 3))\n exp\nendm\n\n; mov instruction with parameters\nup macro\n psh (((($1 & 3) | ($2 << 2)) >> 4) & 15)\n nib (($1 & 3) | ($2 << 2) & 15)\n mov\nendm\n\n; stack template if state is active\n; - last state 1b\n; - end time 4b\n; - last armed sections 1b\n\n; stack template if state is inactive\n; - last state 1b\n; - last armed sections 1b\n\n; BEGIN\n ; pop last state and check it\n ; if last state - active - check time\n ; else - check armed sections\n psh action\n xor b\n pop b\n skz\n bra check_arm\n \ncheck_time\n ; duplicate 'end time'\n dup dw\n ; get current timestamp\n pb TIMESTAMP\n inp b, dw\n ; compare current time and 'end time'\n ; if current time is greater - dispose and set off\n ; else - continue\n cmp dw\n skn\n bra set_on ; +\n pop dw ; - dispose 'end time'\n bra set_off\n\ncheck_arm\n ; get mask of armed sections and duplicate it\n pb ARMED_SECTIONS\n inp b, b\n dup b\n pb sections \n and b ; find mask of armed monitored sections\n up b, 2 ; bring 'last armed sections' to top\n ; compare 'last armed sections' and 'armed monitored sections'\n ; if equal - nothing has changed, continue\n ; else - start timer and set on\n cmp b\n skz\n bra start_timer ; 1 state changed\n pb sections ; 0 same\n and b ; find mask of armed monitored sections\n bra set_off\n \nstart_timer\n pb sections\n and b ; find mask of armed monitored sections\n ; get current timestamp\n psh TIMESTAMP\n inp b, dw\n ; push 'duration - 1' to stack\n pw timer - 1\n ex w, dw, 1\n add dw ; calculate 'end time'\n bra set_on\n \nset_off\n psh ~action & 1\n ret\n \nset_on\n psh action & 1\n ret","params":[{"name":"timer","format":"^([0-9]|[1-8][0-9]|9[0-9]|[1-8][0-9]{2}|9[0-8][0-9]|99[0-9]|[1-8][0-9]{3}|9[0-8][0-9]{2}|99[0-8][0-9]|999[0-9]|10[0-7][0-9]{2}|10800)$","type":"int","description":[{"iso":"ru","value":"Время (сек)"},{"iso":"en","value":"Time (sec)"},{"iso":"es","value":"Tiempo (seg.)"},{"iso":"de","value":"Zeit (Sek.)"}]},{"name":"action","format":"^([01])$","type":"list","default":"1","extra":[{"name":"turn_on","description":[{"iso":"ru","value":"Включать реле"},{"iso":"en","value":"Turn on "},{"iso":"es","value":"Encender "},{"iso":"de","value":"Relais einschalten"}],"value":"1"},{"name":"turn_off","description":[{"iso":"ru","value":"Выключать реле"},{"iso":"en","value":"Turn off "},{"iso":"es","value":"Apagar "},{"iso":"de","value":"Relais ausschalten"}],"value":"0"}],"description":[{"iso":"ru","value":"Действие"},{"iso":"en","value":"Action"},{"iso":"es","value":"Acción"},{"iso":"de","value":"Aktion"}]},{"name":"sections","format":"^([0-9]|[1-8][0-9]|9[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$","type":"sections","extra":[{"name":"section_type","value":"1"}],"description":[{"iso":"ru","value":"Отслеживаемые разделы"},{"iso":"en","value":"Partitions"},{"iso":"es","value":"Secciones "},{"iso":"de","value":"Überwachte Bereiche"}]}],"firmwares":["67111504","21954820","21889283","21954819","22020353","22020355","22020356","22151427","22216961","22216963","22216964","22282499","22348035","22413571","22479107","22544641","22544643","22610177","22610179","22675715","22741251","39452932","22806787","22872323","39584004","22937859","39649540","23068931","39846148","23134467","39911684","39977220","23265539","40042756","40173828","23527683","23593219","23265537","40435972","23658755","23724291","23789827","23855363","24117507"]}