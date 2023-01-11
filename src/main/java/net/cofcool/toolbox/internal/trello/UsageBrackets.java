package net.cofcool.toolbox.internal.trello;

import com.google.gson.annotations.SerializedName;

public record UsageBrackets(

    @SerializedName("boards")
    int boards
) {
}