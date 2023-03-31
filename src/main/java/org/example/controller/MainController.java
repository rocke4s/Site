package org.example.controller;

import com.google.gson.Gson;
import org.example.model.Profile;
import org.example.model.Tasks;
import org.example.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;



@Controller
public class MainController {
    private WebClient webClient;
    private final User user = new User();
    private User shrek = new User();
    private Profile profile = new Profile();
    private Tasks tasks = new Tasks();
    @GetMapping("")
    public String showForm(Model model) {
        System.out.println(Charset.defaultCharset().displayName());
        List<String> list = new ArrayList<>();
        list.add("Сотрудник");
        list.add("Клиент");
        model.addAttribute("user", user);
        model.addAttribute("shrek", shrek);
        model.addAttribute("list",list);
        return "index";
    }
    @GetMapping(value = "/auth")
    public String submitForm(@ModelAttribute("user") User user, Model model) throws IOException {
        webClient = WebClient.create("http://192.168.1.224/franrit/hs/RitExchange/GetGUID/tri/123/");
        Mono<String> body =
                webClient.get()
                        .headers(headers -> headers.setBasicAuth(user.getUsername(), user.getPassword()))
                        .retrieve()
                        .bodyToMono(String.class);
        String str = body.block();
        System.out.println(str);
        Gson g = new Gson();
        profile = g.fromJson(str, Profile.class);
        if(user.isAuth()) {
            System.out.println(user.getUsername() + ", status= "+shrek.isAuth());
            if(user.getTypeUser().equalsIgnoreCase("Сотрудник")){
                return "login_client_success";//переход на страницу сотрудника после входа
            }
            else if(user.getTypeUser().equalsIgnoreCase("Клиент")){
                this.user.setTypeUser(user.getTypeUser());
                return "login_user_sucess";//переход на страницу клиента после входа
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
    public String submitForm(@ModelAttribute("Tasks") Tasks tasks, User user, Model model) throws IOException {
        webClient = WebClient.create("http://192.168.1.224/franrit/hs/RitExchange/getDocuments/");
        Mono<String> body2 =webClient.get()
                .headers(headers -> headers.setBasicAuth(user.getUsername(), user.getPassword(), Charset.defaultCharset()))
                .retrieve()
                .bodyToMono(String.class);
        String str = body2.block();
        Gson g = new Gson();
        profile = g.fromJson(str, Profile.class);
        System.out.println(profile.getName());
        model.addAttribute("user", user);
        model.addAttribute("Tasks",tasks);
        return "tasks_client";
    }

}
