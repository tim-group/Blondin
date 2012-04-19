package com.timgroup.blondin.server.status;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.google.common.base.Charsets;
import com.timgroup.status.StatusPage;
import com.timgroup.status.VersionComponent;

public final class BlondinStatus {

    private static final class Anchor {}

    private final StatusPage statusPage = new StatusPage("Blondin");

    public BlondinStatus() {
        this.statusPage.addComponent(new VersionComponent(Anchor.class));
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        statusPage.render(new OutputStreamWriter(outputStream, Charsets.UTF_8.name()));
        outputStream.close();
    }
}