package app.jakobowski.hello;

import app.jakobowski.lang.Lang;
import app.jakobowski.lang.LangRepository;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class HelloServiceTest {
    private final static String WELCOME = "Hello";
    private final static String FALLBACK_ID_WELCOME = "Hola";

    private LangRepository alwaysReturningHelloRepository() {
        return new LangRepository() {
            @Override
            public Optional<Lang> findById(Integer id) {
                return Optional.of(new Lang(null, WELCOME, "-1"));
            }
        };
    }

    private LangRepository fallbackLangIdRepository(){
        return new LangRepository() {
            @Override
            public Optional<Lang> findById(Integer id) {
                if(id.equals(HelloService.FALLBACK_LANG.getId())) {
                    return Optional.of(new Lang(null, FALLBACK_ID_WELCOME, null));
                }
                return Optional.empty();
            }
        };
    }

    @Test
    public void test_prepareGreeting_nullName_returnsGreetingWithFallbackName() throws Exception {
        // given
        var mockRepository = alwaysReturningHelloRepository();
        var SUT = new HelloService(mockRepository);

        // when
        var result = SUT.prepereGreeting(null, "-1" );

        //then
        assertEquals(WELCOME + " " + HelloService.FALLBACK_NAME + "!", result);
    }

    @Test
    public void test_prepareGreeting_name_returnsGreetingWithName() throws Exception {
        // given
        var mockRepository = alwaysReturningHelloRepository();
        var SUT = new HelloService(mockRepository);
        String name = "test";

        // when
        var result = SUT.prepereGreeting(name, "-1");

        //then
        assertEquals(WELCOME + " " + name + "!", result);
    }

    @Test
    public void test_prepareGreeting_nullLang_returnsGreetingWithFallbackIdLang() throws Exception {
        // given
        var mockRepository = fallbackLangIdRepository();
        var SUT = new HelloService(mockRepository);
        String name = "test";

        // when
        var result = SUT.prepereGreeting(null, null);

        //then
        assertEquals(FALLBACK_ID_WELCOME + " " + HelloService.FALLBACK_NAME + "!", result);
    }

    @Test
    public void test_prepareGreeting_textLang_returnsGreetingWithFallbackIdLang() throws Exception {
        // given
        var mockRepository = fallbackLangIdRepository();
        var SUT = new HelloService(mockRepository);
        String name = "test";

        // when
        var result = SUT.prepereGreeting(null, "abc");

        //then
        assertEquals(FALLBACK_ID_WELCOME + " " + HelloService.FALLBACK_NAME + "!", result);
    }

    @Test
    public void test_prepareGreeting_nonExistingLang_returnsGreetingWithFallbackLang() throws Exception {
        // given
        var mockRepository = new LangRepository() {
            @Override
            public Optional<Lang> findById(Integer id) {
                return Optional.empty();
            }
        };
        var SUT = new HelloService(mockRepository);
        String name = "test";

        // when
        var result = SUT.prepereGreeting(null, "-1");

        //then
        assertEquals(HelloService.FALLBACK_LANG.getWelcomeMsg() + " " + HelloService.FALLBACK_NAME + "!", result);
    }
}
