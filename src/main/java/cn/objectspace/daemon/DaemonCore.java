package cn.objectspace.daemon;

import cn.objectspace.daemon.pojo.dto.ReqDto;
import cn.objectspace.daemon.pojo.dto.ResDto;
import cn.objectspace.daemon.pojo.singletonbean.GsonSingleton;
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

//守护进程接收器
public class DaemonCore extends AbstractVerticle {
    public static void main(String[] args) {
        // 创建服务
        DaemonCore verticle = new DaemonCore();
        Vertx vertx = Vertx.vertx();
        // 部署服务，会执行MyHttpServer的start方法
        vertx.deployVerticle(verticle);
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