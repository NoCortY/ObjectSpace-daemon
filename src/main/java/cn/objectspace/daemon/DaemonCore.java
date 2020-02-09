package cn.objectspace.daemon;

import cn.objectspace.daemon.pojo.dto.ReqDto;
import cn.objectspace.daemon.pojo.dto.ResDto;
import cn.objectspace.daemon.pojo.singletonbean.GsonSingleton;
import cn.objectspace.daemon.util.FileUtil;
import cn.objectspace.daemon.util.ServerUtil;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.File;
import java.io.IOException;

//守护进程接收器
public class DaemonCore extends AbstractVerticle {
    public static void main(String[] args) {
        //初始化服务
        init(args[0]);
        // 创建服务
        DaemonCore verticle = new DaemonCore();
        Vertx vertx = Vertx.vertx();
        // 部署服务，会执行MyHttpServer的start方法
        vertx.deployVerticle(verticle);
    }

    private static void init(String arg) {
        //获取操作系统信息
        String OS = System.getProperty("os.name").toLowerCase();
        File file = null;
        if(OS.contains("win")){
            file = new File("C:\\Users\\NoCortY\\Downloads\\hyperic-sigar-1.6.4\\hyperic-sigar-1.6.4\\sigar-bin\\ocdae.os");
            if(!file.exists()){
                try {
                    file.createNewFile();
                    System.out.println("守护线程第一次启动...写入用户id");
                    FileUtil.writeFileAsString(arg,file.getAbsolutePath());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }else{
            file = new File("/usr/lib64/ocdae.os");
            if(!file.exists()){
                try {
                    file.createNewFile();
                    System.out.println("守护线程第一次启动...写入用户id");
                    FileUtil.writeFileAsString(arg,file.getAbsolutePath());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
    public static void heartBeat(){

    }
    @Override
    public void start() throws Exception {

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        //获取Post请求体中的参数必须要写这一句否则空指针
        router.route().handler(BodyHandler.create());

        /*
         * // 处理get请求 router.get("/get").handler(new Handler<RoutingContext>() {
         *
         * @Override public void handle(RoutingContext request) { // TODO Auto-generated
         * method stub String username = request.request().getParam("username"); String
         * password = request.request().getParam("password");
         * System.out.println(username + " " + password);
         * request.response().end("get request success"); }; });
         */
        // 处理post请求
        router.post("/post").handler(new Handler<RoutingContext>() {
            @Override
            public void handle(RoutingContext request) {
                // TODO Auto-generated method stub
                Gson gson = GsonSingleton.getSingleton();
                ResDto resDto = null;
                System.out.println(request);
                ReqDto reqDto = gson.fromJson(request.getBodyAsJson().toString(), ReqDto.class);
                String command = reqDto.getReqCode();
                switch (command){
                    case "serverInfo": {
                        resDto = new ResDto("1001","请求成功",
                                ServerUtil.serverInfoDtoBuilder());
                        HttpServerResponse response = request.response();
                        MultiMap headers = response.headers();
                        headers.set("content-type", "application/json");
                        response.end(gson.toJson(resDto));
                    }
                }
               /* boolean successFlag = false;
                if(CommandPool.CLOSE_MICROSERVICE.contentEquals(command)) {
                    successFlag = Command.closeService(reqDto.getReqData());
                }
                if(successFlag) {
                    resDto = new ResDto("1001","请求成功","have not data");
                }*/
                //request.response().end(gson.toJson(resDto));
            }
        });

        server.requestHandler(router::accept);
        server.listen(7070);
    }
}