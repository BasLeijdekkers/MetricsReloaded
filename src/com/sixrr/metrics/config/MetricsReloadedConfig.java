/*
 * Copyright 2005, Sixth and Red River Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.sixrr.metrics.config;

import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;

@SuppressWarnings({"PublicField"})
public class MetricsReloadedConfig implements JDOMExternalizable {
    public String selectedProfile = "";
    public boolean autoscroll = false;
    public boolean showOnlyWarnings = false;


    public String getSelectedProfile() {
        return selectedProfile;
    }

    public void setSelectedProfile(String selectedProfile) {
        this.selectedProfile = selectedProfile;
    }

    public boolean isAutoscroll() {
        return autoscroll;
    }

    public void setAutoscroll(boolean autoscroll) {
        this.autoscroll = autoscroll;
    }


    public boolean isShowOnlyWarnings() {
        return showOnlyWarnings;
    }

    public void setShowOnlyWarnings(boolean showOnlyWarnings) {
        this.showOnlyWarnings = showOnlyWarnings;
    }

    public void readExternal(Element element) throws InvalidDataException {
        DefaultJDOMExternalizer.readExternal(this, element);

    }

    public void writeExternal(Element element) throws WriteExternalException {
        DefaultJDOMExternalizer.writeExternal(this, element);
    }
}
