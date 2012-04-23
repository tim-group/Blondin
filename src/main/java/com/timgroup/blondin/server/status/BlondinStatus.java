package com.timgroup.blondin.server.status;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.timgroup.status.Component;
import com.timgroup.status.Report;
import com.timgroup.status.Status;
import com.timgroup.status.StatusPage;
import com.timgroup.status.VersionComponent;

public final class BlondinStatus {

    private static final class Anchor {}

    private final StatusPage statusPage = new StatusPage("Blondin");

    public BlondinStatus(Supplier<Iterable<String>> expensiveResourcesListSupplier) {
        this.statusPage.addComponent(new VersionComponent(Anchor.class));
        this.statusPage.addComponent(new ThrottledResourcesListComponent(expensiveResourcesListSupplier));
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        statusPage.render(new OutputStreamWriter(outputStream, Charsets.UTF_8.name()));
        outputStream.close();
    }

    private static final class ThrottledResourcesListComponent extends Component {
        private final Supplier<Iterable<String>> expensiveResourcesListSupplier;

        public ThrottledResourcesListComponent(Supplier<Iterable<String>> expensiveResourcesListSupplier) {
            super("throttled-resources", "Throttled Resources");
            this.expensiveResourcesListSupplier = expensiveResourcesListSupplier;
        }

        @Override
        public Report getReport() {
            return new Report(Status.INFO, Joiner.on(";").join(expensiveResourcesListSupplier.get()));
        }
    }
}