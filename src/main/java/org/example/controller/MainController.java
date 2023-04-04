package org.example.controller;

import com.google.gson.Gson;
import org.example.model.Task;
import org.example.model.Profile;
import org.example.model.Tasks;
import org.example.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



@Controller
public class MainController {
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
        model.addAttribute("user", user);
        model.addAttribute("shrek", shrek);
        model.addAttribute("list",list);
        return "index";
    }

    @GetMapping("/createtask")
    public String createTask(Model model) {
        List<Integer> numbs = new ArrayList<>();
        for(int i = 0;i<list.getTasks().size();i++) {
            numbs.add(Integer.parseInt(list.getTasks().get(i).getTaskNumber()));
        }
        int maxNumb =1+Collections.max(numbs);
        model.addAttribute("newNumber",maxNumb);
        model.addAttribute("user", user);
        model.addAttribute("task", list);
        return "createTask";
    }


    @GetMapping(value = "/auth")
    public String submitForm(@ModelAttribute("user") User user, Model model) throws Exception {
        //webClient = WebClient.create("http://192.168.1.224/franrit/hs/RitExchange/GetGUID/tri/123/");
        Mono<String> body =
                webClient.get()
                        .uri("http://192.168.1.224/franrit/hs/RitExchange/GetGUID/tri/123/")
                        .headers(headers -> headers.setBasicAuth(user.getUsername(), user.getUsername()))
                        .retrieve()
                        .bodyToMono(String.class);
        webClient = WebClient.builder()
                .filter(ExchangeFilterFunctions
                        .basicAuthentication(user.getUsername(), user.getUsername())).build();
        String str = body.block();
        System.out.println(str);
        Gson g = new Gson();
        this.profile= g.fromJson(str,Profile.class);
        if(user.isAuth()) {
            System.out.println(user.getUsername() + ", status= "+shrek.isAuth());
            if(user.getTypeUser().equalsIgnoreCase("Сотрудник")){
                return "login_client_success";//переход на страницу сотрудника после входа
            }
            else if(user.getTypeUser().equalsIgnoreCase("Клиент")){
                this.user.setTypeUser(user.getTypeUser());
                return "login_user_success";//переход на страницу клиента после входа
            }
            else {
                return showForm(model);
            }
        }
        else {
            return showForm(model);//Вывод при неправильном пароле
        }
    }
    @GetMapping(value = "/profile")
    public String submitForm(@ModelAttribute("Profile") Profile prof, User user, Model model) throws IOException {
        model.addAttribute("user", user);
        model.addAttribute("Profile",profile);
        return "profile_client";
    }
    @GetMapping(value = "/tasks")
    public String submitForm(@ModelAttribute("Tasks") Tasks tsk, User user, Model model) throws IOException {
        Mono<String> body2 =webClient.get()
                .uri("http://192.168.1.224/franrit/hs/RitExchange/getDocuments/"+profile.getUidOrg()+"/"+profile.getUidUser()+"/0")
                .headers(headers -> headers.setBasicAuth("User", "User"))
                .retrieve()
                .bodyToMono(String.class);
        Gson g = new Gson();
        String str = body2.block();
        str = str.replaceAll("\"Заявки\"","\"Tasks\"");
        str = str.replaceAll("Ссылка","TaskUrl");
        str = str.replaceAll("Контрагент","TaskPartner");
        str = str.replaceAll("\"СостояниеЗаявки\"","\"TaskStatus\"");
        str = str.replaceAll("ТипЗадания","TypeTask");
        str = str.replaceAll("Важность","TaskImportance");
        str = str.replaceAll("\"Содержание\"","\"TaskContent\"");
        str = str.replaceAll("СрокДо","TaskDeadline");
        str = str.replaceAll("Трудоемкость","TaskIntensity");
        str = str.replaceAll("ID","TaskId");
        str = str.replaceAll("ДатаВыполнено","TaskDataDone");
        str = str.replaceAll("\"СодержаниеЛУВР\"","\"TaskContentLVR\"");
        str = str.replaceAll("Номер","TaskNumber");
        str = str.replaceAll("Дата","TaskData");
        str = str.replaceAll("Сотрудник","TaskEmployee");
        System.out.println(str);
        list = g.fromJson(str, Task.class);
        model.addAttribute("user", user);
        model.addAttribute("Tasks",list);
        return "tasks_client";
    }

}