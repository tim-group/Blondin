package com.timgroup.blondin.server.status;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.timgroup.tucker.info.Component;
import com.timgroup.tucker.info.Report;
import com.timgroup.tucker.info.Status;
import com.timgroup.tucker.info.component.JarVersionComponent;
import com.timgroup.tucker.info.status.StatusPageGenerator;

public final class BlondinStatus {

    private static final class Anchor {}

    private final StatusPageGenerator statusPage = new StatusPageGenerator("Blondin", new JarVersionComponent(Anchor.class));

    public BlondinStatus(Supplier<Iterable<String>> expensiveResourcesListSupplier) {
        this.statusPage.addComponent(new ThrottledResourcesListComponent(expensiveResourcesListSupplier));
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        statusPage.getApplicationReport().render(new OutputStreamWriter(outputStream, Charsets.UTF_8.name()));
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