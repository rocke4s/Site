package org.example.controller;

import com.google.gson.Gson;
import org.example.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;



@Controller
public class MainController {
    private FileUser fileUser = new FileUser();
    private Task listTasks = new Task();
    private final User user = new User();
    private WebClient webClient = WebClient.create();
    private Profile profile = new Profile();
    @GetMapping("")
    public String showLogin(Model model) {
        List<String> list = new ArrayList<>();
     //   list.add("Сотрудник");
        list.add("Клиент");
        user.setAuth(user.isAuth());
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
        model.addAttribute("fileUser",fileUser);
        return "create_task";
    }
    @PostMapping("/")
    public String exit( Model model)
    {
        user.exit();
        return showLogin(model);
    }
    @PostMapping("/create")
    public String postNewTask(@ModelAttribute("newTask") Tasks newTask, FileUser fileUser, Model model) throws IOException {
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
        File directory = new File("\\\\192.168.1.9\\billi\\"+newTask.getNameTask());
        directory.mkdir();
        String fileName = StringUtils.cleanPath(fileUser.getFile().getOriginalFilename());
        try {
            Path path= Paths.get(directory+"\\"+ fileName);
            Files.copy(fileUser.getFile().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mono<String> body =
                webClient.get()
                        .uri("http://192.168.1.224/franrit/hs/RitExchange/GetCreateTask/"+profile.getUidUser()
                                +"/"+newTask.getNameTask()+"/0?File="+newTask.getNameTask()+"\\" + fileName)
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
                    System.out.println(user.getUsername()+" - "+user.getPassword());
                    webClient = WebClient.builder()
                            .filter(ExchangeFilterFunctions
                                    .basicAuthentication(user.getUsername(), user.getPassword())).build();
                    Mono<String> body =
                            webClient.get()
                                    .uri("http://192.168.1.224/franrit/hs/RitExchange/GetGUID/tri/123/")
                                    .retrieve()
                                    .bodyToMono(String.class);
                    System.out.println(body.block());
                    String str = body.block();
                    Gson g = new Gson();
                    this.profile = g.fromJson(str, Profile.class);
                    if(this.profile.getUidUser()!=null) {
                        System.out.println(this.profile.getUidUser());
                        user.setAuth(true);
                        this.user.setAuth(true);
                        return "login_client_success";
                    }
                    else{
                        return falseAuth(model,user.isAuth());
                    }
                } else if (user.getTypeUser().equalsIgnoreCase("Сотрдник")) {
                    this.user.setTypeUser(user.getTypeUser());
                    return "login_user_success";
                } else {
                    return falseAuth(model,user.isAuth());
                }
            }else {
                return falseAuth(model,user.isAuth());
            }
        } catch (Exception ex){
            //тут должно быть логирование
           // return falseAuth(model);
        }
        return falseAuth(model,user.isAuth());
    }

    public String falseAuth(Model model,boolean userAuth){
        user.forgetUser();
        model.addAttribute("user", user);
        return showLogin(model);
    }


    @PostMapping (value = "/profile")
    public String profile(@ModelAttribute("Profile") Profile prof, Model model,boolean userAuth) throws IOException {
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
            str = str.replaceAll("Наименование","NameTask");
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