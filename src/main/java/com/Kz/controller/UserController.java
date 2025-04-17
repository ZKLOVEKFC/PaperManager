package com.Kz.controller;
import com.Kz.pojo.Result;
import com.Kz.pojo.User;
import com.Kz.service.UserService;
import com.Kz.utils.JwtUtil;
import com.Kz.utils.Md5Util;
import com.Kz.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;

//@CrossOrigin
@RestController
@RequestMapping("user")
@Validated
public class UserController {
    //      注入一个redis对象
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserService userService;
    @PostMapping("register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password){

            //查询
            User u = userService.findByUserName(username);
            //注册
            if (u==null){       //查无此人，可以注册
                userService.register(username, password);
                return Result.success();
            }
            else {              //茶油此人，不给注册
                return Result.error("用户名已被占用");
            }
        }
//    @CrossOrigin
    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {
        //根据用户名查询用户
        User loginUser = userService.findByUserName(username);

        //判断该用户是否存在
        if (loginUser == null) {
            return Result.error("用户名错误");
        }

        //判断密码是否正确  loginUser对象中的password是密文
        if (Md5Util.getMD5String(password).equals(loginUser.getPassword())) {
            //登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getId());
            claims.put("username", loginUser.getUsername());
            String token = JwtUtil.genToken(claims);

            //在响应token前，将token存储到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token, token, 60 * 60 * 24 * 100, TimeUnit.SECONDS);

            System.out.println("登陆成功！！！");
            System.out.println(token);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }

        @GetMapping("/userInfo")                            //获取用户信息
        public Result<User> userInfo(@RequestHeader(name = "Authorization") String token){
        
//            Map<String, Object> map = JwtUtil.parseToken(token);        //解析Token，返回包含用户信息的Map
//            String username = (String) map.get("username");                 //从解析后的Map中提取 username

            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String)map.get("username");
            User user = userService.findByUserName(username);       //根据用户名查询用户信息并返回。
            return Result.success(user);
        }

        @PutMapping("/update")                                      //更新用户信息
        public Result update(@RequestBody @Validated User user){
        //      执行更新操作
        userService.update(user);

        return Result.success();
        }

        @PatchMapping("/updateAvatar")                      //更新用户头像
        public Result updateAvatar(@RequestParam @URL String avatarUrl){         //@RequestParam表示要从querl string中获取数据,同时规定avatarURl必须为URL地址
            userService.updateAvatar(avatarUrl);
            return Result.success();
        }

        @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> params, @RequestHeader("Authorization") String token){           //@RequestBody将请求体内的数据转换为map对象
        //手动校验参数
            String oldPwd = params.get("old_pwd");
            String newPwd = params.get("new_pwd");
            String rePwd = params.get("re_pwd");
            if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)){
                return Result.error("缺少必要参数");          //三个参数不能为空
            }
            Map<String, Object>  map = ThreadLocalUtil.get();           //从线程中获取map对象，以通过username查询旧密码
            String username = (String)map.get("username");
            User loginUser = userService.findByUserName(username);
            String JiaMiPwd =loginUser.getPassword();           //JiaMiPwd是MD5加密后的密码
            if (!JiaMiPwd.equals(Md5Util.getMD5String(oldPwd))){             //将用户传入的旧密码进行加密，将加密的结果与数据库中加密的结果进行比对
                return Result.error("原密码不对");
            }
            if (!newPwd.equals(rePwd)){
                return Result.error("两次输入的密码不一致");
            }
            userService.updatePwd(newPwd);

            //      删除对应的旧token
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.getOperations().delete(token);
            return Result.success();
    }

}
