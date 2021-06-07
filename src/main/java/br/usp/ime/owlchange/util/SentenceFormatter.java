/*
 * Copyright 2018-2020 OWL2DL-Change Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package br.usp.ime.owlchange.util;

import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

public class SentenceFormatter {

    private static String addParenthesesIfContainsSpaces(String s) {
        if (s.contains(" "))
            return "(" + s + ")";
        return s;
    }

    // TODO: DATA PROPERTIES

    public static String humanReadable(OWLObject object) {
        if (object instanceof OWLAxiom)
            return humanReadable((OWLAxiom) object);
        if (object instanceof OWLNamedIndividual)
            return humanReadable((OWLNamedIndividual) object);
        if (object instanceof OWLClassExpression)
            return humanReadable((OWLClassExpression) object);
        if (object instanceof OWLObjectPropertyExpression)
            return humanReadable((OWLObjectPropertyExpression) object);
        return object.toString();
    }

    public static String humanReadable(OWLAxiom sentence) {
        if (sentence instanceof OWLDeclarationAxiom) {
            return "Declaration(" + humanReadable(
                    ((OWLDeclarationAxiom) sentence).getEntity()) + ")";
        }
        if (sentence instanceof OWLClassAssertionAxiom) {
            return humanReadable(
                    ((OWLClassAssertionAxiom) sentence).getIndividual()) + " : "
                    + humanReadable(((OWLClassAssertionAxiom) sentence)
                            .getClassExpression());
        }
        if (sentence instanceof OWLSubClassOfAxiom) {
            return humanReadable(((OWLSubClassOfAxiom) sentence).getSubClass())
                    + " ⊑ " + humanReadable(
                            ((OWLSubClassOfAxiom) sentence).getSuperClass());
        }
        if (sentence instanceof OWLEquivalentClassesAxiom) {
            return ((OWLEquivalentClassesAxiom) sentence)
                    .getClassExpressionsAsList().stream()
                    .map(c -> addParenthesesIfContainsSpaces(humanReadable(c)))
                    .collect(Collectors.joining(" ≡ "));
        }
        if (sentence instanceof OWLDisjointClassesAxiom) {
            return "DISJOINT(" + ((OWLDisjointClassesAxiom) sentence)
                    .getClassExpressionsAsList().stream()
                    .map(c -> humanReadable(c))
                    .collect(Collectors.joining(", ")) + ")";
        }
        if (sentence instanceof OWLDisjointUnionAxiom) {
            return humanReadable(
                    ((OWLDisjointUnionAxiom) sentence).getOWLClass())
                    + " = DISJOINT_UNION("
                    + ((OWLDisjointUnionAxiom) sentence).getClassExpressions()
                            .stream().map(c -> humanReadable(c))
                            .collect(Collectors.joining(", "))
                    + ")";
        }
        if (sentence instanceof OWLObjectPropertyAssertionAxiom) {
            return humanReadable(
                    ((OWLObjectPropertyAssertionAxiom) sentence).getSubject())
                    + " "
                    + addParenthesesIfContainsSpaces(humanReadable(
                            ((OWLObjectPropertyAssertionAxiom) sentence)
                                    .getProperty()))
                    + " "
                    + humanReadable(((OWLObjectPropertyAssertionAxiom) sentence)
                            .getObject());
        }
        if (sentence instanceof OWLSubObjectPropertyOfAxiom) {
            return humanReadable(
                    ((OWLSubObjectPropertyOfAxiom) sentence).getSubProperty())
                    + " ⊑ "
                    + humanReadable(((OWLSubObjectPropertyOfAxiom) sentence)
                            .getSuperProperty());
        }
        if (sentence instanceof OWLSubPropertyChainOfAxiom) {
            return ((OWLSubPropertyChainOfAxiom) sentence).getPropertyChain()
                    .stream()
                    .map(r -> addParenthesesIfContainsSpaces(humanReadable(r)))
                    .collect(Collectors.joining(" ∘ ")) + " ⊑ "
                    + humanReadable(((OWLSubPropertyChainOfAxiom) sentence)
                            .getSuperProperty());
        }
        if (sentence instanceof OWLEquivalentObjectPropertiesAxiom) {
            return ((OWLEquivalentObjectPropertiesAxiom) sentence)
                    .getProperties().stream()
                    .map(r -> addParenthesesIfContainsSpaces(humanReadable(r)))
                    .collect(Collectors.joining(" ≡ "));
        }
        if (sentence instanceof OWLInverseObjectPropertiesAxiom) {
            return "INVERSE(" + ((OWLInverseObjectPropertiesAxiom) sentence)
                    .getProperties().stream().map(r -> humanReadable(r))
                    .collect(Collectors.joining(", ")) + ")";
        }
        if (sentence instanceof OWLDisjointObjectPropertiesAxiom) {
            return "DISJOINT(" + ((OWLDisjointObjectPropertiesAxiom) sentence)
                    .getProperties().stream().map(r -> humanReadable(r))
                    .collect(Collectors.joining(", ")) + ")";
        }
        if (sentence instanceof OWLTransitiveObjectPropertyAxiom) {
            return "TRANSITIVE(" + humanReadable(
                    ((OWLTransitiveObjectPropertyAxiom) sentence).getProperty())
                    + ")";
        }
        if (sentence instanceof OWLReflexiveObjectPropertyAxiom) {
            return "REFLEXIVE(" + humanReadable(
                    ((OWLReflexiveObjectPropertyAxiom) sentence).getProperty())
                    + ")";
        }
        if (sentence instanceof OWLIrreflexiveObjectPropertyAxiom) {
            return "IRREFLEXIVE(" + humanReadable(
                    ((OWLIrreflexiveObjectPropertyAxiom) sentence)
                            .getProperty())
                    + ")";
        }
        if (sentence instanceof OWLSymmetricObjectPropertyAxiom) {
            return "SYMMETRIC(" + humanReadable(
                    ((OWLSymmetricObjectPropertyAxiom) sentence).getProperty())
                    + ")";
        }
        if (sentence instanceof OWLAsymmetricObjectPropertyAxiom) {
            return "ASYMMETRIC(" + humanReadable(
                    ((OWLAsymmetricObjectPropertyAxiom) sentence).getProperty())
                    + ")";
        }
        if (sentence instanceof OWLFunctionalObjectPropertyAxiom) {
            return "FUNCTIONAL(" + humanReadable(
                    ((OWLFunctionalObjectPropertyAxiom) sentence).getProperty())
                    + ")";
        }
        if (sentence instanceof OWLInverseFunctionalObjectPropertyAxiom) {
            return "INVERSE_FUNCTIONAL(" + humanReadable(
                    ((OWLInverseFunctionalObjectPropertyAxiom) sentence)
                            .getProperty())
                    + ")";
        }
        if (sentence instanceof OWLObjectPropertyDomainAxiom) {
            return "DOMAIN("
                    + humanReadable(((OWLObjectPropertyDomainAxiom) sentence)
                            .getProperty())
                    + ") = "
                    + humanReadable(((OWLObjectPropertyDomainAxiom) sentence)
                            .getDomain());
        }
        if (sentence instanceof OWLObjectPropertyRangeAxiom) {
            return "DOMAIN("
                    + humanReadable(((OWLObjectPropertyRangeAxiom) sentence)
                            .getProperty())
                    + ") = "
                    + humanReadable(((OWLObjectPropertyRangeAxiom) sentence)
                            .getRange());
        }
        return sentence.toString();
    }

    public static String humanReadable(OWLClassExpression expression) {
        if (expression.isOWLThing())
            return "⊤";
        if (expression.isOWLNothing())
            return "⊥";
        if (expression instanceof OWLObjectUnionOf) {
            OWLObjectUnionOf e = (OWLObjectUnionOf) expression;
            return e.getOperandsAsList().stream()
                    .map(c -> addParenthesesIfContainsSpaces(humanReadable(c)))
                    .collect(Collectors.joining(" ⊔ "));
        }
        if (expression instanceof OWLObjectIntersectionOf) {
            OWLObjectIntersectionOf e = (OWLObjectIntersectionOf) expression;
            return e.getOperandsAsList().stream()
                    .map(c -> addParenthesesIfContainsSpaces(humanReadable(c)))
                    .collect(Collectors.joining(" ⊓ "));
        }
        if (expression instanceof OWLClass) {
            return ((OWLClass) expression).getIRI().getShortForm();
        }
        if (expression instanceof OWLObjectComplementOf) {
            return "¬" + addParenthesesIfContainsSpaces(humanReadable(
                    ((OWLObjectComplementOf) expression).getOperand()));
        }
        if (expression instanceof OWLObjectMinCardinality) {
            return "≥" + ((OWLObjectMinCardinality) expression).getCardinality()
                    + addParenthesesIfContainsSpaces(
                            humanReadable(((OWLObjectMinCardinality) expression)
                                    .getProperty()))
                    + (((OWLObjectMinCardinality) expression).isQualified()
                            ? ("." + addParenthesesIfContainsSpaces(
                                    humanReadable(
                                            ((OWLObjectMinCardinality) expression)
                                                    .getFiller())))
                            : "");
        }
        if (expression instanceof OWLObjectMaxCardinality) {
            return "≤" + ((OWLObjectMaxCardinality) expression).getCardinality()
                    + addParenthesesIfContainsSpaces(
                            humanReadable(((OWLObjectMaxCardinality) expression)
                                    .getProperty()))
                    + (((OWLObjectMaxCardinality) expression).isQualified()
                            ? ("." + addParenthesesIfContainsSpaces(
                                    humanReadable(
                                            ((OWLObjectMaxCardinality) expression)
                                                    .getFiller())))
                            : "");
        }
        if (expression instanceof OWLObjectExactCardinality) {
            return "="
                    + ((OWLObjectExactCardinality) expression).getCardinality()
                    + addParenthesesIfContainsSpaces(humanReadable(
                            ((OWLObjectExactCardinality) expression)
                                    .getProperty()))
                    + (((OWLObjectExactCardinality) expression).isQualified()
                            ? ("." + addParenthesesIfContainsSpaces(
                                    humanReadable(
                                            ((OWLObjectExactCardinality) expression)
                                                    .getFiller())))
                            : "");
        }
        if (expression instanceof OWLObjectSomeValuesFrom) {
            return "∃"
                    + addParenthesesIfContainsSpaces(
                            humanReadable(((OWLObjectSomeValuesFrom) expression)
                                    .getProperty()))
                    + "."
                    + addParenthesesIfContainsSpaces(
                            humanReadable(((OWLObjectSomeValuesFrom) expression)
                                    .getFiller()));
        }
        if (expression instanceof OWLObjectAllValuesFrom) {
            return "∀"
                    + addParenthesesIfContainsSpaces(
                            humanReadable(((OWLObjectAllValuesFrom) expression)
                                    .getProperty()))
                    + "." + addParenthesesIfContainsSpaces(humanReadable(
                            ((OWLObjectAllValuesFrom) expression).getFiller()));
        }
        if (expression instanceof OWLObjectOneOf) {
            return "{" + ((OWLObjectOneOf) expression).getIndividuals().stream()
                    .map(ind -> humanReadable(ind))
                    .collect(Collectors.joining(", ")) + "}";
        }
        if (expression instanceof OWLObjectHasValue) {
            return "∃"
                    + addParenthesesIfContainsSpaces(humanReadable(
                            ((OWLObjectHasValue) expression).getProperty()))
                    + ".{" + humanReadable(
                            ((OWLObjectHasValue) expression).getFiller())
                    + '}';
        }
        return expression.toString();
    }

    public static String humanReadable(OWLObjectPropertyExpression expression) {
        if (expression.isOWLTopObjectProperty())
            return "⊤";
        if (expression.isOWLBottomObjectProperty())
            return "⊥";
        if (expression instanceof OWLObjectProperty) {
            return ((OWLObjectProperty) expression).getIRI().getShortForm();
        }
        if (expression instanceof OWLObjectInverseOf) {
            return addParenthesesIfContainsSpaces(humanReadable(
                    ((OWLObjectInverseOf) expression).getInverse())) + "⁻";
        }
        return expression.toString();
    }

    public static String humanReadable(OWLNamedIndividual individual) {
        return individual.getIRI().getShortForm();
    }

    public static String humanReadableSetOfSentences(Set<OWLAxiom> sentences,
            boolean skipDeclarations) {
        if (sentences == null)
            return "null";
        if (sentences.isEmpty())
            return "{}";
        return "{ " + sentences.stream()
                .filter(sentence -> !skipDeclarations
                        || !(sentence instanceof OWLDeclarationAxiom))
                .map(sentence -> humanReadable(sentence))
                .collect(Collectors.joining(",\n  ")) + "\n}";
    }

    public static String humanReadableSetOfSentences(Set<OWLAxiom> sentences) {
        return humanReadableSetOfSentences(sentences, false);
    }

    public static String humanReadableSetOfSetsOfSentences(
            Set<Set<OWLAxiom>> setsOfsentences, boolean skipDeclarations) {
        if (setsOfsentences == null)
            return "null";
        if (setsOfsentences.isEmpty())
            return "{}";
        StringBuilder sb = new StringBuilder("{\n");
        int n = 0;
        for (Set<OWLAxiom> sentences : setsOfsentences) {
            sb.append("  ").append(
                    humanReadableSetOfSentences(sentences, skipDeclarations)
                            .replaceAll("\n", "\n  "));
            if (++n < setsOfsentences.size())
                sb.append(",\n");
        }
        sb.append("\n}");
        return sb.toString();
    }

    public static String humanReadableSetOfSetsOfSentences(
            Set<Set<OWLAxiom>> setsOfsentences) {
        return humanReadableSetOfSetsOfSentences(setsOfsentences, false);
    }

}
