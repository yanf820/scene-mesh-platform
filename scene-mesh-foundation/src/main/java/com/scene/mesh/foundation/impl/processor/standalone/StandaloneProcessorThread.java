package com.scene.mesh.foundation.impl.processor.standalone;

import com.scene.mesh.foundation.impl.collector.ProxyCollector;
import com.scene.mesh.foundation.impl.processor.ProcessInput;
import com.scene.mesh.foundation.impl.processor.ProcessOutput;

/**
 */
public class StandaloneProcessorThread implements Runnable {

    private StandaloneProcessorAgent processorAgent;

    public StandaloneProcessorThread(StandaloneProcessorAgent processorAgent) {
        this.processorAgent = processorAgent;
    }

    @Override
    public void run() {
        while (!this.processorAgent.isCurrentOrWillStopped()) {
            if (!(this.processorAgent.isAsProducer() && this.processorAgent.isActuatorBusy())) {
                ProcessTask task = null;
                if (!this.processorAgent.isAsProducer()) {
                    task = this.processorAgent.pollTask();
                    if (task == null) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue; //new loop
                    }
                }
                ProcessInput processInput = new ProcessInput();
                processInput.setInputObject(task != null ? task.getTaskObject() : null);
                ProxyCollector proxyCollector = new ProxyCollector(this.processorAgent);
                ProcessOutput processOutput = new ProcessOutput();
                processOutput.setCollector(proxyCollector);

                try {
                    this.processorAgent.getProcessor().process(processInput, processOutput);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (this.processorAgent.isAsProducer() && (proxyCollector.getCollectedSize() == 0)) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
