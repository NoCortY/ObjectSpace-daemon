package cn.objectspace.daemon;

import cn.objectspace.daemon.cache.DaemonCache;
import cn.objectspace.daemon.core.ServerCore;
import cn.objectspace.daemon.pojo.dto.ReqDto;
import cn.objectspace.daemon.pojo.dto.ResDto;
import cn.objectspace.daemon.pojo.singletonbean.GsonSingleton;
import cn.objectspace.daemon.pool.ConstantPool;
import cn.objectspace.daemon.util.FileUtil;
import cn.objectspace.daemon.util.ServerUtil;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//守护进程接收器
public class DaemonCore extends AbstractVerticle {
    public static void main(String[] args) {
        //初始化服务，参数是暂定的
        String[] a = {"3","www.baidu.com"};
        init(a);
        // 创建服务
        DaemonCore verticle = new DaemonCore();
        Vertx vertx = Vertx.vertx();
        //心跳发生器
        heartBeat(vertx);
        // 部署服务，会执行MyHttpServer的start方法
        vertx.deployVerticle(verticle);
    }

    /**
     * @Description: 初始化守护进程
     * @Param: [arg]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/2/11
     */
    private static void init(String[] args) {

    }
    public static void heartBeat(Vertx vertx){

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        String sendHeartBeatUrl = (String) DaemonCache.getCacheMap().get("pingUrl");
        WebClient webClient = WebClient.create(vertx);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("发送心跳到:"+sendHeartBeatUrl);
                webClient.postAbs(sendHeartBeatUrl).sendJson(ServerCore.serverInfoDtoBuilder(), handle->{
                    System.out.println("心跳信号已传送");
                    System.out.println("返回信息:"+handle.result().bodyAsString());
                });
            }
        }, 5, ConstantPool.HEART_BEAT_SEC, TimeUnit.SECONDS);
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
                                ServerCore.serverInfoDtoBuilder());
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