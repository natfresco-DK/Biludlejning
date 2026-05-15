package ek.dk.biludlejning.controller;

import ek.dk.biludlejning.model.AlarmRule;
import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.service.AlarmService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AlarmController {
    private final AlarmService alarmService;
    private static final Logger logger = LoggerFactory.getLogger(AlarmController.class);

    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }


    @GetMapping("/alarm-create")
    public String showAlarmForm(Model model,
                                @SessionAttribute(name = "currentUser", required = false) User currentUser) {
        model.addAttribute("currentUser", currentUser);
        if (!model.containsAttribute("alarmRule")) {
            model.addAttribute("alarmRule", new AlarmRule());
        }
        return "alarm_create";
    }

    @PostMapping("/alarm-create")
    public String createAlarm(@ModelAttribute AlarmRule alarmRule,
                              @SessionAttribute(name = "currentUser", required = false)User currentUser,
                              Model model,
                              RedirectAttributes redirectAttributes){
        model.addAttribute("currentUser", currentUser);

        if (currentUser == null) {
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Alarmen blev oprettet");
        logger.info("Alarm created by user={}", currentUser.getEmail());

        // for now just return to form, save to DB later
        return "redirect:/alarm-create";

    }
}