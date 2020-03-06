package cn.objectspace.daemon;

import cn.objectspace.daemon.cache.DaemonCache;
import cn.objectspace.daemon.core.ServerCore;
import cn.objectspace.daemon.init.DaemonInit;
import cn.objectspace.daemon.init.ServerInit;
import cn.objectspace.daemon.pojo.dto.ReqDto;
import cn.objectspace.daemon.pojo.dto.ResDto;
import cn.objectspace.daemon.pojo.entity.OcdaePO;
import cn.objectspace.daemon.pojo.singletonbean.GsonSingleton;
import cn.objectspace.daemon.pool.ConstantPool;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
* @Description: 守护进程核心
* @Author: NoCortY
* @Date: 2020/3/6
*/
public class DaemonCore extends AbstractVerticle {
    private static DaemonInit daemonInit = new DaemonInit();
    private static ServerInit serverInit = new ServerInit();
    private static Logger logger = LoggerFactory.getLogger(DaemonCore.class);
    public static void main(String[] args) {
        //初始化服务，参数是暂定的
        String[] a = {"3","http://localhost:7000/ObjectCloud/ComCenter/CC/server/ping"};
        if(daemonInit.init(a)&&serverInit.init(a)){
            // 创建服务
            DaemonCore verticle = new DaemonCore();
            //创建vertx
            Vertx vertx = Vertx.vertx();
            //创建DaemonCore
            DaemonCore daemonCore = new DaemonCore();
            //心脏起搏器
            daemonCore.heartBeat(vertx);
            // 部署守护进程，会执行MyHttpServer的start方法
            vertx.deployVerticle(verticle);
        }else{
            logger.error("守护进程启动失败");
            System.exit(1);
        }



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
    private void heartBeat(Vertx vertx){

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        OcdaePO ocdaePO = (OcdaePO) DaemonCache.getCoreMap().get(ConstantPool.OCDAE_CONFIG);
        WebClient webClient = WebClient.create(vertx);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("发送心跳到:"+ocdaePO.getPingUrl());
                webClient.postAbs(ocdaePO.getPingUrl()).sendJson(ServerCore.serverInfoDtoBuilder(), handle->{
                    System.out.println("心跳信号已传送");
                    System.out.println("返回信息:"+handle.result().bodyAsString());
                });
            }
        }, 5, ConstantPool.HEART_BEAT_SEC, TimeUnit.SECONDS);
    }
}