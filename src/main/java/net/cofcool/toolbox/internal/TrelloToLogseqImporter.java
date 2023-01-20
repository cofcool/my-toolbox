package net.cofcool.toolbox.internal;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.cofcool.toolbox.Tool;
import net.cofcool.toolbox.ToolName;
import net.cofcool.toolbox.internal.trello.*;
import org.apache.commons.io.IOUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.*;

public class TrelloToLogseqImporter implements Tool {


    @Override
    public ToolName name() {
        return ToolName.trelloLogseqImporter;
    }

    @Override
    public void run(Args args) throws Exception {
        var path = args.readArg("path").get().val();
        var outPath = args.readArg("outPath").orElse(new Arg("", "."));
        try (var reader = new JsonReader(new FileReader(path))) {
            Trello trello = new Gson().fromJson(reader, Trello.class);
            var name = trello.name();
            for (Map.Entry<String, List<CardsItem>> entry : trello.cards()
                    .stream()
                    .collect(Collectors.groupingBy(a -> {
                        Optional<ActionsItem> cardDate = cardDate(trello, a.id());
                        String date;
                        if (cardDate.isPresent()) {
                            date = cardDate.get().date();
                        } else {
                            date = a.dateLastActivity();
                        }
                        return LocalDateTime.parse(date, OutStr.FORMATTER).getYear() + "";
                    }))
                    .entrySet()) {
                var out = new OutStr();
                for (CardsItem card : entry.getValue()) {
                    var cardBoard = cardList(trello, card.idList());
                    out.block(OutStr.cardTask(cardBoard.name()) + card.name(), 0)
                            .blockRef(OutStr.date(card.dateLastActivity()))
                            .tag(card.badges().votes() > 0 ? "recommend" : "")
                            .tag(cardBoard.name().replace(" ", "-"))
                            .tag(name);
                    for (LabelsItem label : card.labels()) {
                        out.tag(label.name().replace(" ", "-"));
                    }
                    out.breakLine();
                    if (!card.desc().isBlank()) {
                        out.block(card.desc(), 1).breakLine();
                    }

                    for (String id : card.idChecklists()) {
                        var checklist = checkItemsList(trello, id);
                        out.block(checklist.name(), 1).breakLine();
                        for (CheckItemsItem item : checklist.checkItems()) {
                            out.block(OutStr.sateTask(item.state()) + item.name(), 2).breakLine();
                        }
                    }

                    for (ActionsItem actionsItem : actionList(trello, card.id())) {
                        String text = actionsItem.data().text();
                        if (text != null && !text.isBlank()) {
                            out.block(text, 1).blockRef(OutStr.date(actionsItem.date())).breakLine();
                        }
                    }
                }
                String output = outPath.val() + "/" + "trello" + name + "-" + entry.getKey() + ".md";
                try (var writer = new FileWriter(output)) {
                    IOUtils.write(out.toString(), writer);
                    getLogger().info("Generate " + output + " ok");
                }
            }
        }

    }

    @Override
    public String help() {
        return "--path=demo --outPath=test";
    }

    private List<ActionsItem> actionList(Trello trello, String id) {
        return trello.actions().stream()
                .filter(Objects::nonNull).filter(a -> a.data().card() != null)
                .filter(a -> a.data().card().id().equals(id))
                .sorted(Comparator.comparing(ActionsItem::date)).toList();
    }

    private ListsItem cardList(Trello trello, String id) {
        return trello.lists().stream().filter(a -> a.id().equals(id)).findAny().get();
    }

    private Optional<ActionsItem> cardDate(Trello trello, String id) {
        return trello.actions().stream().filter(a -> a.type().equals("createCard")).filter(a -> a.data().card().id().equals(id)).findAny();
    }

    private ChecklistsItem checkItemsList(Trello trello, String id) {
        return trello.checklists().stream().filter(a -> a.id().equals(id)).findAny().get();
    }

    private static class OutStr {

        public static DateTimeFormatter DATE_FORMATTER;

        static {
            var day = new HashMap<Long, String>();
            for (int i = 0; i < 31; i++) {
                var val = i + switch (i) {
                    case 1, 21 ->  "st";
                    case 2, 22 ->  "nd";
                    case 3, 23 ->  "rd";
                    default -> "th";
                };
                day.put((long) i, val);
            }
            var moy = new HashMap<Long, String>();
            moy.put(1L, "Jan");
            moy.put(2L, "Feb");
            moy.put(3L, "Mar");
            moy.put(4L, "Apr");
            moy.put(5L, "May");
            moy.put(6L, "Jun");
            moy.put(7L, "Jul");
            moy.put(8L, "Aug");
            moy.put(9L, "Sep");
            moy.put(10L, "Oct");
            moy.put(11L, "Nov");
            moy.put(12L, "Dec");
            DATE_FORMATTER = new DateTimeFormatterBuilder()
                .appendText(MONTH_OF_YEAR, moy)
                .appendLiteral(' ')
                .appendText(DAY_OF_MONTH, day)
                .appendLiteral(", ")
                .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .toFormatter();
        }
        public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss");
        public static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(ISO_LOCAL_DATE)
                .appendLiteral('T')
                .append(
                        new DateTimeFormatterBuilder()
                                .appendValue(HOUR_OF_DAY, 2)
                                .appendLiteral(':')
                                .appendValue(MINUTE_OF_HOUR, 2)
                                .optionalStart()
                                .appendLiteral(':')
                                .appendValue(SECOND_OF_MINUTE, 2)
                                .optionalStart()
                                .appendFraction(NANO_OF_SECOND, 0, 9, true)
                                .optionalStart()
                                .appendFraction(MILLI_OF_SECOND, 0, 3, true)
                                .appendLiteral("Z")
                                .toFormatter()
                )
                .toFormatter();
        private final StringBuilder builder = new StringBuilder();

        public OutStr append(String val) {
            builder.append(val);
            return this;
        }

        public OutStr blockRef(String val) {
            if (!val.isEmpty()) {
                builder.append(" [[").append(val).append("]]");
            }
            return this;
        }

        public OutStr tag(String val) {
            if (!val.isEmpty()) {
                builder.append(" #").append(val);
            }
            return this;
        }

        public static String cardTask(String val) {
            return switch (val) {
                case "now", "reading", "watching", "doing", "often" -> "DOING ";
                case "waiting" -> "TODO ";
                default -> "DONE ";
            };
        }

        public static String sateTask(String val) {
            return "complete".equals(val) ? "DONE " : "TODO ";
        }

        public static String date(String val) {
            return DATE_FORMATTER.format(LocalDateTime.parse(val, FORMATTER));
        }

        public static String time(String val) {
            return TIME_FORMATTER.format(LocalDateTime.parse(val, FORMATTER));
        }

        public OutStr breakLine() {
            builder.append("\n");
            return this;
        }

        public OutStr block(String val, int tab) {
            if (!val.isBlank()) {
                builder.append(tab == 0 ? "" : ("    ".repeat(Math.max(0, tab)))).append("- ").append(val);
            }
            return this;
        }

        @Override
        public String toString() {
            return builder.toString();
        }
    }
}
