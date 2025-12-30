// Generated from D:/BaiduNetdiskDownload/学习笔记/java/工具算法DEMO/Soap-Search/src/main/resources/Calculator.g4 by ANTLR 4.13.2
package com.antlr.test2;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CalculatorLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, WS=7, FLOAT=8;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "WS", "FLOAT", "DIGIT", 
			"EXPONET"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "'*'", "'/'", "'+'", "'-'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, "WS", "FLOAT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public CalculatorLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Calculator.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\bU\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0004\u0006#\b"+
		"\u0006\u000b\u0006\f\u0006$\u0001\u0006\u0001\u0006\u0001\u0007\u0004"+
		"\u0007*\b\u0007\u000b\u0007\f\u0007+\u0001\u0007\u0001\u0007\u0005\u0007"+
		"0\b\u0007\n\u0007\f\u00073\t\u0007\u0001\u0007\u0003\u00076\b\u0007\u0001"+
		"\u0007\u0001\u0007\u0004\u0007:\b\u0007\u000b\u0007\f\u0007;\u0001\u0007"+
		"\u0003\u0007?\b\u0007\u0001\u0007\u0004\u0007B\b\u0007\u000b\u0007\f\u0007"+
		"C\u0001\u0007\u0003\u0007G\b\u0007\u0003\u0007I\b\u0007\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0003\tO\b\t\u0001\t\u0004\tR\b\t\u000b\t\f\tS\u0000"+
		"\u0000\n\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b"+
		"\u0006\r\u0007\u000f\b\u0011\u0000\u0013\u0000\u0001\u0000\u0003\u0003"+
		"\u0000\t\n\r\r  \u0002\u0000EEee\u0002\u0000++--^\u0000\u0001\u0001\u0000"+
		"\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000"+
		"\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000"+
		"\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000"+
		"\u0000\u000f\u0001\u0000\u0000\u0000\u0001\u0015\u0001\u0000\u0000\u0000"+
		"\u0003\u0017\u0001\u0000\u0000\u0000\u0005\u0019\u0001\u0000\u0000\u0000"+
		"\u0007\u001b\u0001\u0000\u0000\u0000\t\u001d\u0001\u0000\u0000\u0000\u000b"+
		"\u001f\u0001\u0000\u0000\u0000\r\"\u0001\u0000\u0000\u0000\u000fH\u0001"+
		"\u0000\u0000\u0000\u0011J\u0001\u0000\u0000\u0000\u0013L\u0001\u0000\u0000"+
		"\u0000\u0015\u0016\u0005(\u0000\u0000\u0016\u0002\u0001\u0000\u0000\u0000"+
		"\u0017\u0018\u0005)\u0000\u0000\u0018\u0004\u0001\u0000\u0000\u0000\u0019"+
		"\u001a\u0005*\u0000\u0000\u001a\u0006\u0001\u0000\u0000\u0000\u001b\u001c"+
		"\u0005/\u0000\u0000\u001c\b\u0001\u0000\u0000\u0000\u001d\u001e\u0005"+
		"+\u0000\u0000\u001e\n\u0001\u0000\u0000\u0000\u001f \u0005-\u0000\u0000"+
		" \f\u0001\u0000\u0000\u0000!#\u0007\u0000\u0000\u0000\"!\u0001\u0000\u0000"+
		"\u0000#$\u0001\u0000\u0000\u0000$\"\u0001\u0000\u0000\u0000$%\u0001\u0000"+
		"\u0000\u0000%&\u0001\u0000\u0000\u0000&\'\u0006\u0006\u0000\u0000\'\u000e"+
		"\u0001\u0000\u0000\u0000(*\u0003\u0011\b\u0000)(\u0001\u0000\u0000\u0000"+
		"*+\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000\u0000+,\u0001\u0000\u0000"+
		"\u0000,-\u0001\u0000\u0000\u0000-1\u0005.\u0000\u0000.0\u0003\u0011\b"+
		"\u0000/.\u0001\u0000\u0000\u000003\u0001\u0000\u0000\u00001/\u0001\u0000"+
		"\u0000\u000012\u0001\u0000\u0000\u000025\u0001\u0000\u0000\u000031\u0001"+
		"\u0000\u0000\u000046\u0003\u0013\t\u000054\u0001\u0000\u0000\u000056\u0001"+
		"\u0000\u0000\u00006I\u0001\u0000\u0000\u000079\u0005.\u0000\u00008:\u0003"+
		"\u0011\b\u000098\u0001\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000;9\u0001"+
		"\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<>\u0001\u0000\u0000\u0000"+
		"=?\u0003\u0013\t\u0000>=\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000"+
		"?I\u0001\u0000\u0000\u0000@B\u0003\u0011\b\u0000A@\u0001\u0000\u0000\u0000"+
		"BC\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000\u0000CD\u0001\u0000\u0000"+
		"\u0000DF\u0001\u0000\u0000\u0000EG\u0003\u0013\t\u0000FE\u0001\u0000\u0000"+
		"\u0000FG\u0001\u0000\u0000\u0000GI\u0001\u0000\u0000\u0000H)\u0001\u0000"+
		"\u0000\u0000H7\u0001\u0000\u0000\u0000HA\u0001\u0000\u0000\u0000I\u0010"+
		"\u0001\u0000\u0000\u0000JK\u000209\u0000K\u0012\u0001\u0000\u0000\u0000"+
		"LN\u0007\u0001\u0000\u0000MO\u0007\u0002\u0000\u0000NM\u0001\u0000\u0000"+
		"\u0000NO\u0001\u0000\u0000\u0000OQ\u0001\u0000\u0000\u0000PR\u0003\u0011"+
		"\b\u0000QP\u0001\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000SQ\u0001\u0000"+
		"\u0000\u0000ST\u0001\u0000\u0000\u0000T\u0014\u0001\u0000\u0000\u0000"+
		"\f\u0000$+15;>CFHNS\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}