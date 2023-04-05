package org.example.controller;

import com.google.gson.Gson;
import org.example.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@Controller
public class MainController {
    private NewTask newTask = new NewTask();
    private Task list = new Task();
    private final User user = new User();
    private WebClient webClient = WebClient.create();
    private User shrek = new User();
    private Profile profile = new Profile();
    @GetMapping("")
    public String showForm(Model model) {
        List<String> list = new ArrayList<>();
        list.add("Сотрудник");
        list.add("Клиент");
        model.addAttribute("list",list);
        shrek.setAuth(true);
        model.addAttribute("user", user);
        model.addAttribute("shrek", shrek);
        return "index";
    }

    @GetMapping("/create_task")
    public String createTask(@ModelAttribute("newTask") NewTask newTask, Model model) {
////        Mono<String> body2 =webClient.get()
////                .uri("http://192.168.1.224/franrit/hs/RitExchange/getDocuments/"+profile.getUidOrg()+"/"+profile.getUidUser()+"/0")
////                .retrieve()
////                .bodyToMono(String.class);
//        Gson g = new Gson();
//        String str = body2.block();
        model.addAttribute("user", user);
        model.addAttribute("task", list);
        List<String> listImportance = new ArrayList<>();
        listImportance.add("Высокая");
        listImportance.add("Средняя");
        listImportance.add("Низкая");
        model.addAttribute("newTask",newTask);
        model.addAttribute("listImportance",listImportance);
        return "create_task";
    }

    @GetMapping("/create")
    public String postNewTask(@ModelAttribute("newTask") NewTask newTask,Model model) {
        model.addAttribute("newTask",newTask);
        model.addAttribute("user", user);
        model.addAttribute("task", list);
        return "login_client_success";
    }

    @GetMapping(value = "/auth")
    public String submitForm(@ModelAttribute("user") User user, Model model) throws Exception {
        if(user.getTypeUser()!=null) {
            if (user.getTypeUser().equalsIgnoreCase("Сотрудник")) {
                Mono<String> body =
                        webClient.get()
                                .uri("http://192.168.1.224/franrit/hs/RitExchange/GetGUID/tri/123/")
                                .headers(headers -> headers.setBasicAuth(user.getUsername(), user.getPassword()))
                                .retrieve()
                                .bodyToMono(String.class);
                webClient = WebClient.builder()
                        .filter(ExchangeFilterFunctions
                                .basicAuthentication(user.getUsername(), user.getPassword())).build();
                String str = body.block();
                Gson g = new Gson();
                this.profile = g.fromJson(str, Profile.class);
                System.out.println(user.getUsername() + ", status= " + shrek.isAuth());
                user.setAuth(true);
                this.user.setAuth(true);
                return "login_client_success";//переход на страницу сотрудника после входа
            } else if (user.getTypeUser().equalsIgnoreCase("Клиент")) {
                this.user.setTypeUser(user.getTypeUser());
                return "login_user_success";//переход на страницу клиента после входа
            } else {
                return showForm(model);
            }
        }else {
            return showForm(model);
        }
    }
//        }
//        else {
//            return showForm(model);//Вывод при неправильном пароле
//        }
    @GetMapping(value = "/profile")
    public String submitForm(@ModelAttribute("Profile") Profile prof, Model model) throws IOException {
        System.out.println(user.isAuth());
        model.addAttribute("user", user);
        model.addAttribute("Profile",profile);
        if(user.isAuth()){
            return "profile_client";
        }
        else {
            return showForm(model);

        }
    }
    @GetMapping(value = "/tasks")
    public String submitForm(@ModelAttribute("Tasks") Tasks tsk,Model model) throws IOException {
        if(user.isAuth()) {
            Mono<String> body2 = webClient.get()
                    .uri("http://192.168.1.224/franrit/hs/RitExchange/getDocuments/" + profile.getUidOrg() + "/" + profile.getUidUser() + "/0")
                    .retrieve()
                    .bodyToMono(String.class);
            Gson g = new Gson();
            String str = body2.block();
            str = str.replaceAll("\"Заявки\"", "\"Tasks\"");
            str = str.replaceAll("Ссылка", "TaskUrl");
            str = str.replaceAll("Контрагент", "TaskPartner");
            str = str.replaceAll("\"СостояниеЗаявки\"", "\"TaskStatus\"");
            str = str.replaceAll("ТипЗадания", "TypeTask");
            str = str.replaceAll("Важность", "TaskImportance");
            str = str.replaceAll("\"Содержание\"", "\"TaskContent\"");
            str = str.replaceAll("СрокДо", "TaskDeadline");
            str = str.replaceAll("Трудоемкость", "TaskIntensity");
            str = str.replaceAll("ID", "TaskId");
            str = str.replaceAll("ДатаВыполнено", "TaskDataDone");
            str = str.replaceAll("\"СодержаниеЛУВР\"", "\"TaskContentLVR\"");
            str = str.replaceAll("Номер", "TaskNumber");
            str = str.replaceAll("Дата", "TaskData");
            str = str.replaceAll("Сотрудник", "TaskEmployee");
            list = g.fromJson(str, Task.class);
            model.addAttribute("user", user);
            model.addAttribute("Tasks", list);
            return "tasks_client";
        }else {
            return showForm(model);
        }
    }

}