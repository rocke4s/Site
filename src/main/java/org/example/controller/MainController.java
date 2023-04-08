package org.example.controller;

import com.google.gson.Gson;
import org.example.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@Controller
public class MainController {
    private NewTask newTask = new NewTask();
    private Task listTasks = new Task();
    private final User user = new User();
    private WebClient webClient = WebClient.create();
    private User shrek = new User();
    private Profile profile = new Profile();
    private kek kek1 = new kek();
    @GetMapping("")
    public String showLogin(Model model) {
        List<String> list = new ArrayList<>();
     //   list.add("Сотрудник");
        list.add("Клиент");
        model.addAttribute("list",list);
        shrek.setAuth(true);
        model.addAttribute("user", user);
        model.addAttribute("shrek", shrek);
        return "index";
    }

    @PostMapping("/create_task")
    public String createTask( Model model) {
////        Mono<String> body2 =webClient.get()
////                .uri("http://192.168.1.224/franrit/hs/RitExchange/getDocuments/"+profile.getUidOrg()+"/"+profile.getUidUser()+"/0")
////                .retrieve()
////                .bodyToMono(String.class);
//        Gson g = new Gson();
//        String str = body2.block();
        model.addAttribute("user", user);
        model.addAttribute("task", listTasks);
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
        System.out.println(newTask.getNameTask()+" "+newTask.getContentTask()+" "+newTask.getImportance()+profile.getUidUser());
        switch (newTask.getImportance())
        {
            case "Высокая":
                newTask.setImportance("2");
                break;
            case "Средняя":
                newTask.setImportance("1");
                break;
            case "Низкая":
                newTask.setImportance("0");
                break;
            default:
                break;
        }
        Mono<String> body =
                webClient.get()
                        .uri("http://192.168.1.224/franrit/hs/RitExchange/GetCreateTask/"+profile.getUidUser()
                                +"/"+newTask.getNameTask()+"/"+newTask.getContentTask()+"/"+newTask.getImportance())
      //                  .body(Mono.just(newTask), NewTask.class)
                        .retrieve()
                        .bodyToMono(String.class);
        Gson g = new Gson();
        String str = body.block();
        System.out.println(str);
        model.addAttribute("newTask",newTask);
        model.addAttribute("user", user);
        model.addAttribute("task", listTasks);
        return "login_client_success";
    }

    @PostMapping(value = "/auth")
    public String auth(@ModelAttribute("user") User user, Model model) throws Exception {
        try{
            if(user.getTypeUser()!=null) {
                if (user.getTypeUser().equalsIgnoreCase("Клиент")) {
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
                    if(this.profile.getUidUser()!=null) {
                        System.out.println(user.getUsername() + ", status= " + shrek.isAuth());
                        user.setAuth(true);
                        this.user.setAuth(true);
                        return "login_client_success";//переход на страницу сотрудника после входа
                    }
                    else{
                        shrek.setAuth(false);
                        return showLogin(model);
                    }
                } else if (user.getTypeUser().equalsIgnoreCase("Сотрудник")) {
                    this.user.setTypeUser(user.getTypeUser());
                    return "login_user_success";//переход на страницу клиента после входа
                } else {
                    shrek.setAuth(false);
                    model.addAttribute("shrek", shrek);
                    return showLogin(model);
                }
            }else {
                shrek.setAuth(false);
                model.addAttribute("shrek", shrek);
                return showLogin(model);
            }
        } catch (Exception ex)
        {
            shrek.setAuth(false);
            model.addAttribute("shrek", shrek);
            return showLogin(model);
        }

    }
    @PostMapping (value = "/profile")
    public String profile(@ModelAttribute("Profile") Profile prof, Model model) throws IOException {
        System.out.println(user.isAuth());
        model.addAttribute("user", user);
        model.addAttribute("Profile",profile);
        if(user.isAuth()){
            return "profile_client";
        }
        else {
            return showLogin(model);

        }
    }
    @PostMapping(value = "/tasks")
    public String tasks(@ModelAttribute("tsk") Tasks tsk,Model model) throws IOException {
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
            listTasks = g.fromJson(str, Task.class);
            model.addAttribute("user", user);
            model.addAttribute("Tasks", listTasks);
            model.addAttribute("kek1",kek1);
            return "tasks_client";
        }else {
            return showLogin(model);
        }
    }
    @PostMapping(value = "/changestatus")
    public String changeStatus(@ModelAttribute("kek1") kek kek1,Tasks tsk,Model model) throws IOException {
       Mono<String> body = webClient.get()
                .uri("http://192.168.1.224/franrit/hs/RitExchange/GetTestResult/" + kek1.getKkk()+ "/8")
                        .retrieve()
                                .bodyToMono(String.class);
       String str = body.block();
        System.out.println(str);
        model.addAttribute("user", user);
        model.addAttribute("Tasks", listTasks);
        model.addAttribute("kek1",kek1);
       return tasks(tsk,model);
    }
}