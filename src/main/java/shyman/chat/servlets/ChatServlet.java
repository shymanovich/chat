package shyman.chat.servlets;

import shyman.chat.classes.DBWorker;
import shyman.chat.classes.Message;
import shyman.chat.classes.MessageExchange;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@WebServlet(value = "/chat")
public class ChatServlet extends HttpServlet {

    DBWorker dbWorker;

    private MessageExchange messageExchange;

    Logger logger;

    @Override
    public void init() throws ServletException {
        super.init();
        messageExchange = new MessageExchange();
        dbWorker = new DBWorker();
        createLogger();
    }

    private void createLogger() {
        logger = Logger.getLogger(this.getServletName());
        FileHandler fh;
        try {
            fh = new FileHandler("LogFile.log", true);
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
            fh.setFormatter(new SimpleFormatter());
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String response = messageExchange.getServerResponse(dbWorker);
        resp.getOutputStream().println(response);
        logger.log(Level.INFO, "Response sent, size " + response.length());
        logger.log(Level.INFO, "End request " + req.getRequestedSessionId());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Message message = null;
        try {
            message = messageExchange.getClientMessage(req.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "New message: " + message.toString());
        dbWorker.addMessage(message);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Message message = null;
        try {
            message = messageExchange.getClientMessage(req.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Update message: " + message.toString());

        message = dbWorker.updateMessage(message.getId(), message.getText());
        if (message == null) {
            logger.log(Level.WARNING, "Invalid message id");
            resp.getWriter().println(messageExchange.getErrorMessage("Invalid message id"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Message message = null;
        try {
            message = messageExchange.getClientMessage(req.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Delete message: " + message.toString());

        message = dbWorker.deleteMessage(message.getId());
        if(message == null) {
            logger.log(Level.WARNING, "Invalid message id");
            resp.getWriter().println(messageExchange.getErrorMessage("Invalid message id "));
        }
    }
}