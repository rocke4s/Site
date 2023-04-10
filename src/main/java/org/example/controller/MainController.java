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
    private Task listTasks = new Task();
    private final User user = new User();
    private WebClient webClient = WebClient.create();
    private Profile profile = new Profile();
    @GetMapping("")
    public String showLogin(Model model) {
        List<String> list = new ArrayList<>();
     //   list.add("Сотрудник");
        list.add("Клиент");
        model.addAttribute("list",list);
        model.addAttribute("user", user);
        return "index";
    }

    @PostMapping("/create_task")
    public String createTask(Tasks newTask, Model model) {
        model.addAttribute("user", user);
        List<String> listImportance = new ArrayList<>();
        listImportance.add("Высокая");
        listImportance.add("Средняя");
        listImportance.add("Низкая");
        model.addAttribute("newTask",newTask);
        model.addAttribute("listImportance",listImportance);
        return "create_task";
    }
    @PostMapping("/")
    public String exit( Model model)
    {
        user.exit();
        return showLogin(model);
    }
    @GetMapping("/create")
    public String postNewTask(@ModelAttribute("newTask") Tasks newTask,Model model) {
        switch (newTask.getTaskImportance())
        {
            case "Высокая":
                newTask.setTaskImportance("2");
                break;
            case "Средняя":
                newTask.setTaskImportance("1");
                break;
            case "Низкая":
                newTask.setTaskImportance("0");
                break;
            default:
                break;
        }
        Mono<String> body =
                webClient.get()
                        .uri("http://192.168.1.224/franrit/hs/RitExchange/GetCreateTask/"+profile.getUidUser()
                                +"/"+newTask.getNameTask()+"/"+newTask.getTaskContent()+"/"+newTask.getTaskImportance())
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
            if(user.getTypeUser()!=null && user.getTypeUser().equalsIgnoreCase("Клиент") ) {//TODO: check all If example merge if then &&
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
                        System.out.println(user.getUsername() + ", status= " + user.isAuth());
                        user.setAuth(true);
                        this.user.setAuth(true);
                        return "login_client_success";
                    }
                    else{
                        return falseAuth(model);
                    }
                } else if (user.getTypeUser().equalsIgnoreCase("Сотрдник")) {
                    this.user.setTypeUser(user.getTypeUser());
                    return "login_user_success";
                } else {
                    return falseAuth(model);
                }
            }else {
                return falseAuth(model);
            }
        } catch (Exception ex){
            //тут должно быть логирование
           // return falseAuth(model);
        }
        return falseAuth(model);
    }

    public String falseAuth(Model model){
        user.forgetUser();
        model.addAttribute("user", user);
        return showLogin(model);
    }


    @PostMapping (value = "/profile")
    public String profile(@ModelAttribute("Profile") Profile prof, Model model) throws IOException {
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
            return "tasks_client";
        }else {
            return showLogin(model);
        }
    }
    @GetMapping(value = "/changestatus")
    public String changeStatus(String uidDoc_5,String uidDoc_8,Tasks tsk,Model model) throws IOException {
        Mono<String> body = null;
        if(uidDoc_5!=null && !uidDoc_5.isEmpty())//передаем выбранное состояние заявки - "на доработку"
        {
            body = webClient.get()
                    .uri("http://192.168.1.224/franrit/hs/RitExchange/GetTestResult/" + uidDoc_5+ "/5")
                    .retrieve()
                    .bodyToMono(String.class);
        }
        if(uidDoc_8!=null && !uidDoc_8.isEmpty())//передаем выбранное состояние заявки - "выполнено"
        {
            body = webClient.get()
                    .uri("http://192.168.1.224/franrit/hs/RitExchange/GetTestResult/" + uidDoc_8+ "/8")
                    .retrieve()
                    .bodyToMono(String.class);
        }

       String str = body.block();
        System.out.println(str);
        model.addAttribute("user", user);
        model.addAttribute("Tasks", listTasks);
        return tasks(tsk,model);
    }
}