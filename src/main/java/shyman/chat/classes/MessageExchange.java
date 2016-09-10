package shyman.chat.classes;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MessageExchange {

    private JSONParser jsonParser = new JSONParser();

    public String getServerResponse(DBWorker dbWorker) {
        List<Message> messages = new ArrayList<>();
        Stack<Message> stack = dbWorker.getMessages();
        int n = stack.size();
        for (int i = 1; i <= n; i++) {
            messages.add(stack.pop());
        }
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("messages", messages);

        return jsonObject.toJSONString();
    }

    public Message getClientMessage(InputStream inputStream) throws Exception {
        JSONObject json = getJSONObject(inputStreamToString(inputStream));
        return new Message(new Long(json.get("id").toString()), json.get("txt").toString(), json.get("author").toString());
    }

    public JSONObject getJSONObject(String json) throws ParseException {
        return (JSONObject) jsonParser.parse(json.trim());
    }

    public String inputStreamToString(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = in.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }

        System.out.println("Input stream " + new String(baos.toByteArray()));
        return new String(baos.toByteArray());
    }

    public String getErrorMessage(String text) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", text);
        return jsonObject.toJSONString();
    }
}