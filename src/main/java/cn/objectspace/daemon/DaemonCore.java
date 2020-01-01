package cn.objectspace.daemon;

import com.google.gson.Gson;

import cn.objectspace.daemon.command.Command;
import cn.objectspace.daemon.pojo.dto.ReqDto;
import cn.objectspace.daemon.pojo.dto.ResDto;
import cn.objectspace.daemon.pojo.singletonbean.GsonSingleton;
import cn.objectspace.daemon.pool.CommandPool;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

//�ػ����̽�����
public class DaemonCore extends AbstractVerticle {
	public static void main(String[] args) {
		// ��������
		DaemonCore verticle = new DaemonCore();
		Vertx vertx = Vertx.vertx();
		// ������񣬻�ִ��MyHttpServer��start����
		vertx.deployVerticle(verticle);
	}

	@Override
	public void start() throws Exception {

		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);
		
		/*
		 * // ����get���� router.get("/get").handler(new Handler<RoutingContext>() {
		 * 
		 * @Override public void handle(RoutingContext request) { // TODO Auto-generated
		 * method stub String username = request.request().getParam("username"); String
		 * password = request.request().getParam("password");
		 * System.out.println(username + " " + password);
		 * request.response().end("get request success"); }; });
		 */
		// ����post����
		router.post("/post").handler(new Handler<RoutingContext>() {
			@Override
			public void handle(RoutingContext request) {
				// TODO Auto-generated method stub
				Gson gson = GsonSingleton.getSingleton();
				ResDto resDto = null;
				String reqJson = request.getBody().toJsonObject().toString();
				ReqDto reqDto = gson.fromJson(reqJson, ReqDto.class);
				String command = reqDto.getReqCode();
				boolean successFlag = false;
				if(CommandPool.CLOSE_MICROSERVICE.contentEquals(command)) {
					successFlag = Command.closeService(reqDto.getReqData());
				}
				if(successFlag) {
					resDto = new ResDto("1001","����ɹ�","have not data");
				}
				request.response().end(gson.toJson(resDto));
			}
		});

		server.requestHandler(router::accept);
		server.listen(80);
	}
}