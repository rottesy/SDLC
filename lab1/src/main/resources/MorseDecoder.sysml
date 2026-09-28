// SysML v2 — утилита декодирования Азбуки Морзе

package MorseDecoder {
    part def MorseModel {
        attribute encodedText;
        attribute result;
        port notify;
    }

    part def MainFrame {
        port listen;
        port userAction;
    }

    part def InputDialog {
        port inputData;
    }

    part def MorseController {
        port handle;
    }

    part def MorseDecoderSystem {
        attribute status;
    }

    action def DecodeMorse {
        in encodedText;
        out decodedResult;
    }
}