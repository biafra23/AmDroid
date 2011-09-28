package com.jaeckel.amdroid.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaeckel.amdroid.api.model.DateSerializer;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.api.model.User;
import junit.framework.TestCase;

import java.util.Date;

/**
 * User: biafra
 * Date: 9/28/11
 * Time: 10:05 PM
 */
public class GsonTests extends TestCase {


  private Gson gson;

  @Override
  public void setUp() {
    gson = new GsonBuilder()
      .registerTypeAdapter(Date.class, new DateSerializer())
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .serializeNulls()
      .create();

  }

  @Override
  public void tearDown() {

  }


  public void testDeserializeUser() {

    System.out.println("testDeserializeUser");
    
    String json = "{id=1465, name='Janina Gold', picture='https://graph.facebook.com/604996344/picture'}";
    User user = gson.fromJson(json, User.class);

    System.out.println("user: " + user);

    assertNull(user.getCreatedAt());
    assertEquals("https://graph.facebook.com/604996344/picture", user.getPicture());
  }

  public void testDeserializeUser2() {

    String json = "{\"id\":14283,\"name\":\"Foo Bart\",\"picture\":\"\",\"created_at\":\"2011-09-26T07:30:34Z\",\"created_statements_count\":0,\"given_amen_count\":3,\"received_amen_count\":0,\"followers_count\":1,\"following_count\":19,\"following\":false,\"recent_amen\":[]}";

    User user = gson.fromJson(json, User.class);

    System.out.println("-->user: " + user);

    assertNotNull("No id", user.getId());
    assertNotNull("No name", user.getName());
    assertNotNull("No picture", user.getPicture());
    assertNotNull("No created_at", user.getCreatedAt());
    assertEquals("Wrong createdAd", 1317022234000L, user.getCreatedAt().getTime());


  }

  public void testDeserializeTopic() {

    String json = "{\"best\":true,\"description\":\"News\",\"scope\":\"This Week\",\"id\":30280,\"amen_count\":2,\"objekts_count\":1,\"ranked_statements\":[{\"rank\":0,\"statement\":{\"id\":81601,\"objekt\":{\"key\":[\"local\",\"58783\"],\"name\":\"The New Markthalle Opening This Weekend\",\"kind_id\":2},\"total_amen_count\":2,\"agreeing_network\":[{\"id\":1,\"name\":\"Florian Weber\",\"picture\":\"https://graph.facebook.com/1050621902/picture\"},{\"id\":72,\"name\":\"Lina Kunimoto\",\"picture\":\"https://graph.facebook.com/536582454/picture\"}],\"agreeable\":true,\"first_poster\":{\"id\":72,\"name\":\"Lina Kunimoto\",\"picture\":\"https://graph.facebook.com/536582454/picture\"},\"first_posted_at\":\"2011-09-28T16:28:20Z\",\"first_amen_id\":197102,\"topic\":{\"best\":true,\"description\":\"News\",\"scope\":\"This Week\",\"id\":30280,\"objekts_count\":1}}}],\"as_sentence\":\"The Best News This Week\"}";


    Topic topic = gson.fromJson(json, Topic.class);

    System.out.println("Topic: " + topic);

  }


  public void testDeserializeTopic2() {

    String json = "{\n" +
                  "            \"best\": false, \n" +
                  "            \"description\": \"Complex Of Bad Taste\", \n" +
                  "            \"id\": 30317, \n" +
                  "            \"objekts_count\": 1, \n" +
                  "            \"scope\": \"Ever\"\n" +
                  "        }";

    Topic topic = gson.fromJson(json, Topic.class);

    System.out.println("Topic: " + topic);
  }

  public void testDeserializeStatement() {

    String json = "{\n" +
                  "        \"agreeable\": false, \n" +
                  "        \"agreeing_network\": [\n" +
                  "            {\n" +
                  "                \"id\": 14283, \n" +
                  "                \"name\": \"Foo Bart\", \n" +
                  "                \"picture\": \"\"\n" +
                  "            }, \n" +
                  "            {\n" +
                  "                \"id\": 3, \n" +
                  "                \"name\": \"Felix \\\"Chief Executive Amenator\\\" Petersen\", \n" +
                  "                \"picture\": \"https://graph.facebook.com/500959222/picture\"\n" +
                  "            }, \n" +
                  "            {\n" +
                  "                \"id\": 1064, \n" +
                  "                \"name\": \"Sebastian Socha\", \n" +
                  "                \"picture\": \"https://graph.facebook.com/727645502/picture\"\n" +
                  "            }, \n" +
                  "            {\n" +
                  "                \"id\": 1465, \n" +
                  "                \"name\": \"Janina Gold\", \n" +
                  "                \"picture\": \"https://graph.facebook.com/604996344/picture\"\n" +
                  "            }, \n" +
                  "            {\n" +
                  "                \"id\": 1931, \n" +
                  "                \"name\": \"Stefan Baumgartl\", \n" +
                  "                \"picture\": \"https://graph.facebook.com/100000281183013/picture\"\n" +
                  "            }, \n" +
                  "            {\n" +
                  "                \"id\": 5113, \n" +
                  "                \"name\": \"Ronald Erb\", \n" +
                  "                \"picture\": \"https://graph.facebook.com/1451809666/picture\"\n" +
                  "            }, \n" +
                  "            {\n" +
                  "                \"id\": 6175, \n" +
                  "                \"name\": \"Namri Dagyab\", \n" +
                  "                \"picture\": \"https://graph.facebook.com/622475586/picture\"\n" +
                  "            }, \n" +
                  "            {\n" +
                  "                \"id\": 9092, \n" +
                  "                \"name\": \"Marvin\", \n" +
                  "                \"picture\": \"https://graph.facebook.com/1618571897/picture\"\n" +
                  "            }\n" +
                  "        ], \n" +
                  "        \"id\": 81704, \n" +
                  "        \"objekt\": {\n" +
                  "            \"key\": [\n" +
                  "                \"local\", \n" +
                  "                \"58860\"\n" +
                  "            ], \n" +
                  "            \"kind_id\": 2, \n" +
                  "            \"name\": \"ALEXA, the \\\"shopping, entertainment dine, fun & wine\\\" center, where you can find sleazy hookers wear but no tight, black, sexy jeans \"\n" +
                  "        }, \n" +
                  "        \"topic\": {\n" +
                  "            \"best\": false, \n" +
                  "            \"description\": \"Complex Of Bad Taste\", \n" +
                  "            \"id\": 30317, \n" +
                  "            \"objekts_count\": 1, \n" +
                  "            \"scope\": \"Ever\"\n" +
                  "        }, \n" +
                  "        \"total_amen_count\": 8\n" +
                  "    }";

    Statement statement = gson.fromJson(json, Statement.class);

    System.out.println("Statement: " + statement);


    assertEquals("", false, (boolean) statement.isAgreeable());
    assertNotNull("", statement.getAgreeingNetwork());
  }
}
